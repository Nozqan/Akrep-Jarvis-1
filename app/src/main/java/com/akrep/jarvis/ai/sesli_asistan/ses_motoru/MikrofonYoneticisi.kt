package com.akrep.jarvis.ai.sesli_asistan.ses_motoru

data class SesDurumu(
    val dinliyor: Boolean = false,
    val taninanMetin: String = "",
    val hata: String? = null,
)
