package com.nebiozkan.akrepjarvis.veri.depo

import android.content.Context
import com.nebiozkan.akrepjarvis.veri.model.NotKaydi
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class NotYoneticisi(context: Context) {
    private val dosya = File(context.filesDir, "akrep_jarvis_notlar.json")

    fun hepsi(): List<NotKaydi> =
        runCatching {
            if (!dosya.exists()) return@runCatching emptyList()
            val dizi = JSONArray(dosya.readText())
            buildList {
                for (i in 0 until dizi.length()) {
                    val nesne = dizi.getJSONObject(i)
                    add(
                        NotKaydi(
                            id = nesne.getLong("id"),
                            metin = nesne.getString("metin"),
                            olusturmaZamani = nesne.getLong("zaman"),
                        ),
                    )
                }
            }.sortedByDescending { it.olusturmaZamani }
        }.getOrElse { emptyList() }

    fun ekle(metin: String): NotKaydi {
        val kayit = NotKaydi(
            id = System.currentTimeMillis(),
            metin = metin.trim(),
            olusturmaZamani = System.currentTimeMillis(),
        )
        kaydet(hepsi() + kayit)
        return kayit
    }

    fun ara(sorgu: String): List<NotKaydi> =
        hepsi().filter { it.metin.contains(sorgu, ignoreCase = true) }

    fun sil(id: Long) {
        kaydet(hepsi().filterNot { it.id == id })
    }

    fun sonNot(): NotKaydi? = hepsi().firstOrNull()

    private fun kaydet(notlar: List<NotKaydi>) {
        val dizi = JSONArray()
        notlar.forEach {
            dizi.put(
                JSONObject()
                    .put("id", it.id)
                    .put("metin", it.metin)
                    .put("zaman", it.olusturmaZamani),
            )
        }
        dosya.writeText(dizi.toString())
    }
}
