package com.nebiozkan.akrepjarvis.canli_kamera

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaramaArsivi(private val context: Context) {
    fun kaydet(metin: String): Result<File> = runCatching {
        val tarih = SimpleDateFormat("yyyyMMdd_HHmmss", Locale("tr", "TR")).format(Date())
        val dosya = File(context.filesDir, "tarama_$tarih.txt")
        dosya.writeText(
            "AKREP JARVIS tarama sonucu\n" +
                "Tarih: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("tr", "TR")).format(Date())}\n\n" +
                metin,
        )
        dosya
    }

    fun hepsi(): List<File> =
        context.filesDir.listFiles()
            ?.filter { it.name.startsWith("tarama_") && it.extension == "txt" }
            ?.sortedByDescending { it.lastModified() }
            .orEmpty()

    fun temizle() {
        hepsi().forEach(File::delete)
    }
}
