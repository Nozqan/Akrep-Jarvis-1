package com.akrep.jarvis.ai.ozellikler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OzellikDeposuTest {
    @Test
    fun toplamOzelikSayisi55Olmali() {
        assertEquals(55, OzellikDeposu.tumOzellikler.size)
    }

    @Test
    fun tumOzellikAnahtarlariBenzersizOlmali() {
        val anahtarlar = OzellikDeposu.tumOzellikler.map { it.anahtar }
        assertEquals(anahtarlar.size, anahtarlar.toSet().size)
    }

    @Test
    fun temelAlanlarBosOlmali() {
        assertTrue(OzellikDeposu.alanicOzellikler(OzellikAlani.CANLI_GORUS).isNotEmpty())
        assertTrue(OzellikDeposu.alanicOzellikler(OzellikAlani.GOREV).isNotEmpty())
        assertTrue(OzellikDeposu.alanicOzellikler(OzellikAlani.SISTEM).isNotEmpty())
    }
}
