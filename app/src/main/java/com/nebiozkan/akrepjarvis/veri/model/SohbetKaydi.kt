package com.nebiozkan.akrepjarvis.veri.model

data class SohbetKaydi(
    val id: Long,
    val kullaniciMesaji: String,
    val yanit: String,
    val kaynak: String,
    val zaman: Long,
)
