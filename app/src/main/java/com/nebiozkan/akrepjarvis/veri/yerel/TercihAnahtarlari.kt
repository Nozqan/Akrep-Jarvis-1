package com.nebiozkan.akrepjarvis.veri.yerel

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object TercihAnahtarlari {
    val saglayici = stringPreferencesKey("saglayici")
    val hariciYapayZeka = booleanPreferencesKey("harici_yapay_zeka")
    val sesliYanit = booleanPreferencesKey("sesli_yanit")
    val tamGiris = booleanPreferencesKey("tam_giris_animasyonu")
    val hareketAzaltma = booleanPreferencesKey("hareket_azaltma")
}
