package com.akrep.jarvis.ai.sesli_asistan.ses_motoru

object UyandiriciKelimeMotoru {
    fun temizle(metin: String): String =
        metin
            .replace(Regex("(?i)^\\s*(jarvis|akrep)\\b[,.]?\\s*"), "")
            .trim()

    fun uyandırmaAlgilandi(metin: String): Boolean =
        Regex("(?i)\\b(jarvis|akrep)\\b").containsMatchIn(metin)
}
