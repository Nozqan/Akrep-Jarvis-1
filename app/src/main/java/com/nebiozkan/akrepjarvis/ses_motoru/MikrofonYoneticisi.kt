package com.nebiozkan.akrepjarvis.ses_motoru

data class SesDurumu(
    val dinliyor: Boolean = false,
    val taninanMetin: String = "",
    val hata: String? = null,
)
