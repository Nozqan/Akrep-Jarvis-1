package com.nebiozkan.akrepjarvis.ana_hud

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.nebiozkan.akrepjarvis.sistem_izleyici.SistemIzleyici
import com.nebiozkan.akrepjarvis.sistem_izleyici.SistemDurumu

class AnaHudViewModel(application: Application) : AndroidViewModel(application) {
    var durum by mutableStateOf("Hazır")
        private set

    var sistemDurumu by mutableStateOf(SistemDurumu())
        private set

    private val sistemIzleyici = SistemIzleyici(application)

    init {
        sistemDurumu = sistemIzleyici.guncelDurumuAl()
    }
}
