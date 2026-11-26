package com.akrep.jarvis.ai.yapay_zeka

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import com.akrep.jarvis.ai.veri.depo.NotYoneticisi
import com.akrep.jarvis.ai.gorev_merkezi.HesapMakinesi
import java.text.Normalizer

data class YerelKomutSonucu(
    val yanit: String,
    val yerelCozuldu: Boolean,
    val eylem: String? = null,
)

class YerelKomutMotoru(
    private val context: Context,
    private val notYoneticisi: NotYoneticisi,
) {
    fun calistir(metin: String): YerelKomutSonucu {
        val komut = normalize(metin)

        return when {
            komut.contains("fener") && komut.contains("ac") -> {
                val acildi = fenerAyarla(true)
                YerelKomutSonucu(
                    if (acildi) "Fener açıldı." else "Bu cihazdan fener açılamadı.",
                    true,
                )
            }
            komut.contains("fener") && (komut.contains("kapat") || komut.contains("sondur")) -> {
                val kapandi = fenerAyarla(false)
                YerelKomutSonucu(
                    if (kapandi) "Fener kapatıldı." else "Bu cihazdan fener kapatılamadı.",
                    true,
                )
            }
            komut.contains("batarya") || komut.contains("pil") -> YerelKomutSonucu(
                "Batarya durumu: ${bataryaDurumu()}.",
                true,
            )
            komut.contains("depolama") -> YerelKomutSonucu(
                "Depolama durumu: ${depolamaDurumu()}.",
                true,
            )
            komut.contains("internet") || komut.contains("ag durumu") -> YerelKomutSonucu(
                agDurumu(),
                true,
            )
            komut.contains("uygulama durumu") -> YerelKomutSonucu(
                "AKREP JARVIS çalışıyor.",
                true,
            )
            komut.contains("son not") || komut.contains("notlari oku") -> {
                val sonNot = notYoneticisi.sonNot()
                YerelKomutSonucu(
                    sonNot?.metin ?: "Henüz kayıtlı bir not yok.",
                    true,
                )
            }
            komut.contains("not olustur") || komut.contains("not tut") -> {
                val icerik = Regex("(?i).*(?:not olustur|not tut)[,:]?\\s*(.*)")
                    .find(metin)
                    ?.groupValues
                    ?.getOrNull(1)
                    ?.trim()
                    .orEmpty()
                    .ifBlank { "Sesli veya metinle doldurulmamış not" }
                notYoneticisi.ekle(icerik)
                YerelKomutSonucu("Not oluşturuldu: $icerik", true)
            }
            komut.contains("hesapla") || komut.contains("hesap") -> {
                val sonuc = hesapla(metin)
                YerelKomutSonucu(
                    sonuc?.let { "Sonuç: $it" } ?: "Bu işlemi güvenli biçimde hesaplayamadım.",
                    sonuc != null,
                )
            }
            komut.contains("kamera") -> {
                YerelKomutSonucu("Kamera ekranı açılıyor.", true, "kamera")
            }
            komut.contains("ayar") -> {
                context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                YerelKomutSonucu("Ayarlar açıldı.", true, "ayarlar")
            }
            komut.contains("gorev") || komut.contains("not merkezi") -> {
                YerelKomutSonucu("Görev merkezi açılıyor.", true, "gorevler")
            }
            komut.contains("gizlilik") -> {
                YerelKomutSonucu("Gizlilik merkezi açılıyor.", true, "gizlilik")
            }
            else -> YerelKomutSonucu(
                "Bu komutu çevrimdışı yerel motorla anlayamadım.",
                false,
            )
        }
    }

    fun normalize(metin: String): String =
        Normalizer.normalize(metin, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()

    private fun fenerAyarla(acik: Boolean): Boolean =
        try {
            val kameraYoneticisi = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val kameraId = kameraYoneticisi.cameraIdList.firstOrNull {
                kameraYoneticisi.getCameraCharacteristics(it).get(
                    android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE,
                ) == true
            } ?: return false
            kameraYoneticisi.setTorchMode(kameraId, acik)
            true
        } catch (_: Exception) {
            false
        }

    private fun bataryaDurumu(): String {
        val intent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return "Kullanılamıyor"
        val seviye = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val olcek = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (seviye < 0 || olcek <= 0) return "Kullanılamıyor"
        return "%${seviye * 100 / olcek}"
    }

    private fun depolamaDurumu(): String {
        val alan = StatFs(Environment.getDataDirectory().path)
        val toplam = alan.totalBytes
        val bos = alan.availableBytes
        val gb = 1024L * 1024L * 1024L
        return "${(toplam - bos) / gb} GB kullanılan, ${bos / gb} GB boş"
    }

    private fun agDurumu(): String {
        val yonetici = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as? android.net.ConnectivityManager ?: return "Ağ durumu kullanılamıyor"
        val ag = yonetici.activeNetwork ?: return "İnternet bağlantısı yok"
        val ozellikler = yonetici.getNetworkCapabilities(ag)
            ?: return "Ağ bilgisi kullanılamıyor"
        if (!ozellikler.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            return "İnternet bağlantısı doğrulanmadı"
        }
        return when {
            ozellikler.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi bağlı"
            ozellikler.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobil veri bağlı"
            else -> "Ağ bağlantısı mevcut"
        }
    }

    private fun hesapla(metin: String): Double? {
        val ifade = metin
            .lowercase()
            .replace("topla", "+")
            .replace("cikar", "-")
            .replace("eksi", "-")
            .replace("carp", "*")
            .replace("bol", "/")
            .substringAfter("hesapla", "")
            .substringAfter("hesap", "")
        return HesapMakinesi.hesapla(ifade)
    }
}
