package com.akrep.jarvis.ai.veri.depo

import android.content.Context
import com.akrep.jarvis.ai.veri.model.SohbetKaydi
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class SohbetGecmisiYoneticisi(context: Context) {
    private val dosya = File(context.filesDir, "akrep_jarvis_sohbet.json")

    fun ekle(kullaniciMesaji: String, yanit: String, kaynak: String): SohbetKaydi {
        val kayit = SohbetKaydi(
            id = System.currentTimeMillis(),
            kullaniciMesaji = kullaniciMesaji,
            yanit = yanit,
            kaynak = kaynak,
            zaman = System.currentTimeMillis(),
        )
        kaydet(listOf(kayit) + hepsi())
        return kayit
    }

    fun hepsi(): List<SohbetKaydi> = runCatching {
        if (!dosya.exists()) return@runCatching emptyList()
        val dizi = JSONArray(dosya.readText())
        buildList {
            for (i in 0 until dizi.length()) {
                val nesne = dizi.getJSONObject(i)
                add(
                    SohbetKaydi(
                        id = nesne.getLong("id"),
                        kullaniciMesaji = nesne.getString("kullanici"),
                        yanit = nesne.getString("yanit"),
                        kaynak = nesne.getString("kaynak"),
                        zaman = nesne.getLong("zaman"),
                    ),
                )
            }
        }
    }.getOrElse { emptyList() }

    fun temizle() {
        if (dosya.exists()) dosya.delete()
    }

    private fun kaydet(kayitlar: List<SohbetKaydi>) {
        val dizi = JSONArray()
        kayitlar.take(200).forEach {
            dizi.put(
                JSONObject()
                    .put("id", it.id)
                    .put("kullanici", it.kullaniciMesaji)
                    .put("yanit", it.yanit)
                    .put("kaynak", it.kaynak)
                    .put("zaman", it.zaman),
            )
        }
        dosya.writeText(dizi.toString())
    }
}
