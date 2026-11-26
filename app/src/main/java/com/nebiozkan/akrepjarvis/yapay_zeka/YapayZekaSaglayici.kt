package com.nebiozkan.akrepjarvis.yapay_zeka

data class YapayZekaIstegi(
    val mesaj: String,
)

data class YapayZekaYaniti(
    val metin: String,
)

sealed class BaglantiDurumu {
    data object Bagli : BaglantiDurumu()
    data object BagliDegil : BaglantiDurumu()
    data class Hata(val mesaj: String) : BaglantiDurumu()
}

interface YapayZekaSaglayici {
    suspend fun yanitOlustur(istek: YapayZekaIstegi): Result<YapayZekaYaniti>
    suspend fun baglantiKontrolu(): BaglantiDurumu
}
