package com.nebiozkan.akrepjarvis.yapay_zeka

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class GuvenliAnahtarDeposu(context: Context) {
    private val anahtar = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "akrep_jarvis_guvenli",
        anahtar,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun kaydet(anahtarAdi: String, deger: String) {
        prefs.edit().putString(anahtarAdi, deger).apply()
    }

    fun oku(anahtarAdi: String): String? = prefs.getString(anahtarAdi, null)

    fun sil(anahtarAdi: String) {
        prefs.edit().remove(anahtarAdi).apply()
    }
}
