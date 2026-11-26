package com.nebiozkan.akrepjarvis.ana_hud

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nebiozkan.akrepjarvis.sistem_izleyici.SistemIzleyici
import com.nebiozkan.akrepjarvis.sistem_izleyici.SistemDurumu
import com.nebiozkan.akrepjarvis.veri.depo.NotYoneticisi
import com.nebiozkan.akrepjarvis.yapay_zeka.KomutYonlendirici
import com.nebiozkan.akrepjarvis.gunes_hud.HudDurumu
import com.nebiozkan.akrepjarvis.veri.depo.SohbetGecmisiYoneticisi
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
    }

    fun hataAyarla(metin: String) {
        hataMesaji = metin
        hudDurumu = HudDurumu.HATA
    }

    fun eylemiTuket() {
        eylem = null
    }
}
