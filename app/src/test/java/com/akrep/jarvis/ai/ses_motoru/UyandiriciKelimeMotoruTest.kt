package com.akrep.jarvis.ai.sesli_asistan.ses_motoru

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UyandiriciKelimeMotoruTest {
    @Test
    fun jarvis_kelimesi_algilanmali() {
        assertTrue(UyandiriciKelimeMotoru.uyandırmaAlgilandi("Jarvis batarya durumunu göster"))
    }

    @Test
    fun uyandirma_kelimesi_cikarilmali() {
        assertEquals(
            "batarya durumunu göster",
            UyandiriciKelimeMotoru.temizle("Akrep batarya durumunu göster"),
        )
    }

    @Test
    fun farkli_kelime_algilanmamali() {
        assertFalse(UyandiriciKelimeMotoru.uyandırmaAlgilandi("hava durumu"))
    }
}
