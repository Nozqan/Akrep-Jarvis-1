package com.nebiozkan.akrepjarvis.ses_motoru

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TurkceMetinSeslendirici(context: Context) : TextToSpeech.OnInitListener {
    private val motor = TextToSpeech(context, this)
    var kullanimaHazir = false
        private set
    var yayinSonaErdi: (() -> Unit)? = null

    override fun onInit(durum: Int) {
        kullanimaHazir = durum == TextToSpeech.SUCCESS
        if (kullanimaHazir) {
            motor.language = Locale("tr", "TR")
            motor.setOnUtteranceProgressListener(
                object : UtteranceProgressListener() {
                    override fun onStart(id: String?) = Unit
                    override fun onDone(id: String?) { yayinSonaErdi?.invoke() }
                    @Deprecated("Deprecated in Java")
                    override fun onError(id: String?) { yayinSonaErdi?.invoke() }
                },
            )
        }
    }

    fun konuş(metin: String) {
        if (!kullanimaHazir || metin.isBlank()) {
            yayinSonaErdi?.invoke()
            return
        }
        val parametreler = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "akrep_jarvis")
        }
        motor.stop()
        motor.speak(metin, TextToSpeech.QUEUE_FLUSH, parametreler, "akrep_jarvis")
    }

    fun durdur() {
        motor.stop()
    }

    fun serbestBirak() {
        motor.stop()
        motor.shutdown()
    }
}
