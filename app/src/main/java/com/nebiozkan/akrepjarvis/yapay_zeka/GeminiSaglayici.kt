package com.nebiozkan.akrepjarvis.yapay_zeka

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiSaglayici(
    private val anahtar: String?,
    private val model: String,
) : YapayZekaSaglayici {
    private val istemci = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override suspend fun baglantiKontrolu(): BaglantiDurumu = withContext(Dispatchers.IO) {
        if (anahtar.isNullOrBlank()) BaglantiDurumu.Hata("API anahtarı tanımlı değil.")
        else BaglantiDurumu.Bagli
    }

    override suspend fun yanitOlustur(istek: YapayZekaIstegi): Result<YapayZekaYaniti> =
        withContext(Dispatchers.IO) {
            if (anahtar.isNullOrBlank()) {
                return@withContext Result.failure(IllegalStateException("API anahtarı tanımlı değil"))
            }
            val modelAdi = model.ifBlank { "gemini-1.5-flash" }
            val govde = JSONObject()
                .put(
                    "contents",
                    JSONArray().put(
                        JSONObject().put(
                            "parts",
                            JSONArray().put(JSONObject().put("text", "Türkçe kısa yanıt: " + istek.mesaj)),
                        ),
                    ),
                )

            val url =
                "https://generativelanguage.googleapis.com/v1beta/models/$modelAdi:generateContent?key=$anahtar"
            val httpIstek = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(govde.toString().toRequestBody("application/json".toMediaType()))
                .build()

            runCatching {
                istemci.newCall(httpIstek).execute().use { yanit ->
                    val metin = yanit.body?.string().orEmpty()
                    if (!yanit.isSuccessful) {
                        throw IllegalStateException("Sağlayıcı HTTP ${yanit.code} yanıtı verdi")
                    }
                    val icerik = JSONObject(metin)
                        .optJSONArray("candidates")
                        ?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")
                        .orEmpty()
                    if (icerik.isBlank()) throw IllegalStateException("Sağlayıcı boş yanıt verdi")
                    YapayZekaYaniti(icerik)
                }
            }
        }
}
