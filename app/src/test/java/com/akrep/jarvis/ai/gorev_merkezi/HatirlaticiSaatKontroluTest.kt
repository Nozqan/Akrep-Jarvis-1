package com.akrep.jarvis.ai.gorev_merkezi

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HatirlaticiSaatKontroluTest {
    @Test
    fun gecerliSaatVeDakikayiKabulEder() {
        assertTrue(HatirlaticiSaatKontrolu.gecerliMi(0, 0))
        assertTrue(HatirlaticiSaatKontrolu.gecerliMi(23, 59))
    }

    @Test
    fun aralikDisiSaatVeDakikayiReddeder() {
        assertFalse(HatirlaticiSaatKontrolu.gecerliMi(-1, 30))
        assertFalse(HatirlaticiSaatKontrolu.gecerliMi(24, 30))
        assertFalse(HatirlaticiSaatKontrolu.gecerliMi(12, -1))
        assertFalse(HatirlaticiSaatKontrolu.gecerliMi(12, 60))
        assertFalse(HatirlaticiSaatKontrolu.gecerliMi(null, 30))
        assertFalse(HatirlaticiSaatKontrolu.gecerliMi(12, null))
    }
}
