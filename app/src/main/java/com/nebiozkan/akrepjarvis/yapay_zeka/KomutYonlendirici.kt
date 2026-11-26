package com.nebiozkan.akrepjarvis.yapay_zeka

import android.content.Context
import com.nebiozkan.akrepjarvis.ayarlar.AyarlarDeposu
import com.nebiozkan.akrepjarvis.veri.depo.NotYoneticisi
import kotlinx.coroutines.flow.first

data class KomutYaniti(
    val metin: String,
    val kaynak: String,
    val eylem: String? = null,
)

class KomutYonlendirici(context: Context) {
    private val anahtarDeposu = GuvenliAnahtarDeposu(context)
    private val ayarlarDeposu = AyarlarDeposu(context)
    private val notYoneticisi = NotYoneticisi(context)
    private val yerelMotor = YerelKomutMotoru(context, notYoneticisi)

    fun yerelKomutuCalistir(metin: String): YerelKomutSonucu =
        yerelMotor.calistir(metin)

    suspend fun yanitOlustur(metin: String): KomutYaniti {
        val yerel = yerelKomutuCalistir(metin)
        if (yerel.yerelCozuldu) {
            return KomutYaniti(yerel.yanit, "Çevrimdışı yerel komut", yerel.eylem)
        }

        val ayarlar = ayarlarDeposu.ayarlar.first()
        if (!ayarlar.hariciYapayZekaIzni) {
            return KomutYaniti(
                "Bu bilgi veya işlem için internet bağlantısı ve harici yapay zekâ izni gerekiyor.",
                "Çevrimdışı yerel komut",
            )
        }

        val saglayici: YapayZekaSaglayici = when (ayarlar.saglayici) {
            "Gemini" -> GeminiSaglayici(
                anahtarDeposuOku("gemini_api_key"),
                ayarlar.model,
            )
            else -> OpenAiUyumluSaglayici(
                anahtarDeposuOku("openai_api_key"),
                ayarlar.ucNokta,
                ayarlar.model,
            )
        }

        val sonuc = saglayici.yanitOlustur(YapayZekaIstegi(metin))
        return sonuc.fold(
            onSuccess = { KomutYaniti(it.metin, ayarlar.saglayici) },
            onFailure = {
                KomutYaniti(
                    "Yapay zekâ yanıtı alınamadı. İnterneti ve API ayarlarını kontrol edin.",
                    "Bağlantı hatası",
                )
            },
        )
    }

    fun apiAnahtariKaydet(saglayici: String, anahtar: String) {
        val anahtarAdi = if (saglayici == "Gemini") "gemini_api_key" else "openai_api_key"
        anahtarDeposu.kaydet(anahtarAdi, anahtar)
    }

    fun apiAnahtariOku(saglayici: String): String? {
        val anahtarAdi = if (saglayici == "Gemini") "gemini_api_key" else "openai_api_key"
        return anahtarDeposuOku(anahtarAdi)
    }

    fun apiAnahtariSil(saglayici: String) {
        val anahtarAdi = if (saglayici == "Gemini") "gemini_api_key" else "openai_api_key"
        anahtarDeposu.sil(anahtarAdi)
    }

    suspend fun baglantiKontrolu(): BaglantiDurumu {
        val ayarlar = ayarlarDeposu.ayarlar.first()
        val saglayici: YapayZekaSaglayici = when (ayarlar.saglayici) {
            "Gemini" -> GeminiSaglayici(
                anahtarDeposuOku("gemini_api_key"),
                ayarlar.model,
            )
            else -> OpenAiUyumluSaglayici(
                anahtarDeposuOku("openai_api_key"),
                ayarlar.ucNokta,
                ayarlar.model,
            )
        }
        return saglayici.baglantiKontrolu()
    }

    private fun anahtarDeposuOku(anahtarAdi: String): String? =
        anahtarDeposu.oku(anahtarAdi)
}
