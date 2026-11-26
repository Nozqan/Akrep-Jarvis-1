package com.akrep.jarvis.ai.ayarlar

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.ayarlarDataStore by preferencesDataStore("akrep_jarvis_ayarlar")

data class Ayarlar(
    val saglayici: String = "Yerel",
    val ucNokta: String = "https://api.openai.com/v1",
    val model: String = "gpt-4o-mini",
    val hariciYapayZekaIzni: Boolean = false,
    val sesliYanıt: Boolean = true,
    val tamGirisAnimasyonu: Boolean = true,
    val hareketAzaltma: Boolean = false,
)

class AyarlarDeposu(private val context: Context) {
    private object Anahtarlar {
        val saglayici = stringPreferencesKey("saglayici")
        val ucNokta = stringPreferencesKey("uc_nokta")
        val model = stringPreferencesKey("model")
        val hariciIzni = booleanPreferencesKey("harici_yapay_zeka")
        val sesliYanit = booleanPreferencesKey("sesli_yanit")
        val tamGiris = booleanPreferencesKey("tam_giris_animasyonu")
        val hareketAzaltma = booleanPreferencesKey("hareket_azaltma")
    }

    val ayarlar: Flow<Ayarlar> = context.ayarlarDataStore.data.map { tercih ->
        Ayarlar(
            saglayici = tercih[Anahtarlar.saglayici] ?: "Yerel",
            ucNokta = tercih[Anahtarlar.ucNokta] ?: "https://api.openai.com/v1",
            model = tercih[Anahtarlar.model] ?: "gpt-4o-mini",
            hariciYapayZekaIzni = tercih[Anahtarlar.hariciIzni] ?: false,
            sesliYanıt = tercih[Anahtarlar.sesliYanit] ?: true,
            tamGirisAnimasyonu = tercih[Anahtarlar.tamGiris] ?: true,
            hareketAzaltma = tercih[Anahtarlar.hareketAzaltma] ?: false,
        )
    }

    suspend fun saglayiciAyarla(deger: String) =
        context.ayarlarDataStore.edit { it[Anahtarlar.saglayici] = deger }

    suspend fun ucNoktaAyarla(deger: String) =
        context.ayarlarDataStore.edit { it[Anahtarlar.ucNokta] = deger }

    suspend fun modelAyarla(deger: String) =
        context.ayarlarDataStore.edit { it[Anahtarlar.model] = deger }

    suspend fun hariciIzniAyarla(deger: Boolean) =
        context.ayarlarDataStore.edit { it[Anahtarlar.hariciIzni] = deger }

    suspend fun sesliYanitiAyarla(deger: Boolean) =
        context.ayarlarDataStore.edit { it[Anahtarlar.sesliYanit] = deger }

    suspend fun tamGirisAyarla(deger: Boolean) =
        context.ayarlarDataStore.edit { it[Anahtarlar.tamGiris] = deger }

    suspend fun hareketAzaltmaAyarla(deger: Boolean) =
        context.ayarlarDataStore.edit { it[Anahtarlar.hareketAzaltma] = deger }
}
