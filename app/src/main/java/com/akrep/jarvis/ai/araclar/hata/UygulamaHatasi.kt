package com.akrep.jarvis.ai.araclar.hata

sealed class UygulamaHatasi(open val mesaj: String) {
    data class Izin(override val mesaj: String) : UygulamaHatasi(mesaj)
    data class Kamera(override val mesaj: String) : UygulamaHatasi(mesaj)
    data class Ses(override val mesaj: String) : UygulamaHatasi(mesaj)
    data class Ag(override val mesaj: String) : UygulamaHatasi(mesaj)
    data class Yerel(override val mesaj: String) : UygulamaHatasi(mesaj)
}
