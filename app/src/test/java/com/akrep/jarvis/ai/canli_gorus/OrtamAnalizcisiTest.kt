package com.akrep.jarvis.ai.canli_gorus

import com.akrep.jarvis.ai.canli_gorus.canli_kamera.OrtamAnalizcisi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OrtamAnalizcisiTest {
    @Test
    fun nesneYoksaAcikMesajVerir() {
        val sonuc = OrtamAnalizcisi().analizEt(emptyList())
        assertTrue(sonuc.contains("algılanmadı"))
    }

    @Test
    fun nesnelerTurkceMetneDonusur() {
        val sonuc = OrtamAnalizcisi().analizEt(listOf("Kişi", "Telefon"))
        assertEquals("Algılanan ortam ögeleri: Kişi, Telefon.", sonuc)
    }
}
