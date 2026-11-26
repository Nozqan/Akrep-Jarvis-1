package com.nebiozkan.akrepjarvis.gorev_merkezi

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HesapMakinesiTest {
    @Test
    fun toplama_calismali() {
        assertEquals(12.0, HesapMakinesi.hesapla("7 + 5"), 0.001)
    }

    @Test
    fun carpma_cikarmadan_once_calismali() {
        assertEquals(14.0, HesapMakinesi.hesapla("2 + 3 * 4"), 0.001)
    }

    @Test
    fun sifira_bolunme_geri_donmemeli() {
        assertNull(HesapMakinesi.hesapla("8 / 0"))
    }

    @Test
    fun metin_ifade_bos_sonuc_donmeli() {
        assertNull(HesapMakinesi.hesapla("hava durumu"))
    }
}
