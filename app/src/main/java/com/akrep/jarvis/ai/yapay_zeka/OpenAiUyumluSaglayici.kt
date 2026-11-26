package com.akrep.jarvis.ai.yapay_zeka

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OpenAiUyumluSaglayici(
    private val anahtar: String?,
    private val ucNokta: String,
    private val model: String,
) : YapayZekaSaglayici {
    private val istemci = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override suspend fun baglantiKontrolu(): BaglantiDurumu = withContext(Dispatchers.IO) {
        if (anahtar.isNullOrBlank()) return@withContext BaglantiDurumu.Hata("API anahtarı tanımlı değil.")
        val temelAdres = ucNokta.trim()
        if (!temelAdres.startsWith("https://") && !temelAdres.startsWith("http://")) {
            return@withContext BaglantiDurumu.Hata("API uç noktası geçerli bir HTTP adresi değil.")
        }
        try {
            val istek = Request.Builder()
                .url(temelAdres.trimEnd('/') + "/models")
                .addHeader("Authorization", "Bearer $anahtar")
                .get()
                .build()
            istemci.newCall(istek).execute().use { yanit ->
                if (yanit.isSuccessful) BaglantiDurumu.Bagli
                else BaglantiDurumu.Hata("Bağlantı testi başarısız: HTTP ${yanit.code}")
            }
        } catch (_: Exception) {
            BaglantiDurumu.Hata("Sağlayıcıya bağlanılamadı.")
        }
    }

    override suspend fun yanitOlustur(istek: YapayZekaIstegi): Result<YapayZekaYaniti> =
        withContext(Dispatchers.IO) {
            if (anahtar.isNullOrBlank()) {
                return@withContext Result.failure(IllegalStateException("API anahtarı tanımlı değil"))
            }
            val temelAdres = ucNokta.trim()
            if (!temelAdres.startsWith("https://") && !temelAdres.startsWith("http://")) {
                return@withContext Result.failure(IllegalArgumentException("API uç noktası geçerli değil"))
            }
            val govde = JSONObject()
                .put("model", model.ifBlank { "gpt-4o-mini" })
                .put(
                    "messages",
                    JSONArray().put(
                        JSONObject()
                            .put("role", "system")
                            .put("content", "Türkçe, kısa ve anlaşılır yanıt ver."),
                    ).put(
                        JSONObject()
                            .put("role", "user")
                            .put("content", istek.mesaj),
                    ),
                )

            val httpIstek = Request.Builder()
                .url(temelAdres.trimEnd('/') + "/chat/completions")
                .addHeader("Authorization", "Bearer $anahtar")
                .addHeader("Content-Type", "application/json")
                .post(govde.toString().toRequestBody("application/json".toMediaType()))
                .build()

            runCatching {
                istemci.newCall(httpIstek).execute().use { yanit ->
                    val metin = yanit.body?.string().orEmpty()
                    if (!yanit.isSuccessful) {
                        throw IllegalStateException("Sağlayıcı HTTP ${yanit.code} yanıtı verdi")
                    }
                    val yanitJson = JSONObject(metin)
                    val icerik = yanitJson
                        .optJSONArray("choices")
                        ?.optJSONObject(0)
                        ?.optJSONObject("message")
                        ?.optString("content")
                        .orEmpty()
                    if (icerik.isBlank()) throw IllegalStateException("Sağlayıcı boş yanıt verdi")
                    YapayZekaYaniti(icerik)
                }
            }
        }
}
