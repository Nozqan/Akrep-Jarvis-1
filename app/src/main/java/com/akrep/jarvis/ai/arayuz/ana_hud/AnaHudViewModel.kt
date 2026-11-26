package com.akrep.jarvis.ai.arayuz.ana_hud

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.akrep.jarvis.ai.sistem_izleyici.SistemIzleyici
import com.akrep.jarvis.ai.sistem_izleyici.SistemDurumu
import com.akrep.jarvis.ai.veri.depo.NotYoneticisi
import com.akrep.jarvis.ai.yapay_zeka.KomutYonlendirici
import com.akrep.jarvis.ai.arayuz.gunes_hud.HudDurumu
import com.akrep.jarvis.ai.veri.depo.SohbetGecmisiYoneticisi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnaHudViewModel(application: Application) : AndroidViewModel(application) {
    var durum by mutableStateOf("Hazır")
        private set

    var hudDurumu by mutableStateOf(HudDurumu.HAZIR)
        private set

    var cevap by mutableStateOf("")
        private set

    var komutMetni by mutableStateOf("")
        private set

    var hataMesaji by mutableStateOf<String?>(null)
        private set

    var eylem by mutableStateOf<String?>(null)
        private set

    var sistemDurumu by mutableStateOf(SistemDurumu())
        private set

    private val sistemIzleyici = SistemIzleyici(application)
    private val komutYonlendirici = KomutYonlendirici(application)
    private val notYoneticisi = NotYoneticisi(application)
    private val sohbetGecmisi = SohbetGecmisiYoneticisi(application)

    init {
        sistemDurumu = sistemIzleyici.guncelDurumuAl()
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                sistemDurumu = sistemIzleyici.guncelDurumuAl()
            }
        }
    }

    fun komutMetniAyarla(metin: String) {
        komutMetni = metin
    }

    fun komutGonder(metin: String = komutMetni) {
        if (metin.isBlank()) {
            hataMesaji = "Bir komut veya mesaj yazın."
            hudDurumu = HudDurumu.HATA
            return
        }
        hataMesaji = null
        hudDurumu = HudDurumu.DUSUNUYOR
        viewModelScope.launch {
            val yanit = komutYonlendirici.yanitOlustur(metin)
            cevap = yanit.metin
            eylem = yanit.eylem
            sohbetGecmisi.ekle(metin, yanit.metin, yanit.kaynak)
            komutMetni = ""
            hudDurumu = if (yanit.kaynak == "Bağlantı hatası") HudDurumu.HATA else HudDurumu.HAZIR
        }
    }

    fun sesSonucu(metin: String) {
        komutMetniAyarla(metin)
        komutGonder(metin)
    }

    fun durumAyarla(yeniDurum: HudDurumu) {
        hudDurumu = yeniDurum
        sistemDurumu = sistemDurumu.copy(
            mikrofon = if (yeniDurum == HudDurumu.DINLIYOR) "Dinleme aktif" else "Dinleme kapalı",
            yapayZeka = if (yeniDurum == HudDurumu.DUSUNUYOR) "Yanıt hazırlanıyor" else "Yerel motor hazır",
        )
    }

    fun hataAyarla(metin: String) {
        hataMesaji = metin
        hudDurumu = HudDurumu.HATA
        sistemDurumu = sistemDurumu.copy(mikrofon = "Dinleme kapalı")
    }

    fun eylemiTuket() {
        eylem = null
    }

    fun kameraDurumAyarla(acik: Boolean) {
        sistemDurumu = sistemDurumu.copy(kamera = if (acik) "Kamera aktif" else "Kamera kapalı")
    }
}
