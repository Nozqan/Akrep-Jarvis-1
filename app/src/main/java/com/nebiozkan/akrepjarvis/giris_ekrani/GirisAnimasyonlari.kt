package com.nebiozkan.akrepjarvis.giris_ekrani

object GirisAnimasyonlari {
    fun sureMilisaniye(tamAnimasyon: Boolean): List<Long> =
        if (tamAnimasyon) listOf(900L, 900L, 700L)
        else listOf(450L, 450L, 350L)
}
