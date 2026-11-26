package com.nebiozkan.akrepjarvis.ses_motoru

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class KonusmaTaniyici(
    context: Context,
    private val onSonuc: (String) -> Unit,
    private val onHata: (String) -> Unit,
) {
    private val recognizer = if (SpeechRecognizer.isRecognitionAvailable(context)) {
        SpeechRecognizer.createSpeechRecognizer(context)
    } else {
        null
    }

    fun destekleniyor(): Boolean = recognizer != null

    fun dinlemeyeBasla() {
        if (recognizer == null) {
            onHata("Bu cihazda konuşmadan metne dönüştürme kullanılamıyor.")
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("tr", "TR").toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer.setRecognitionListener(
            object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) = Unit
                override fun onBeginningOfSpeech() = Unit
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray?) = Unit
                override fun onEndOfSpeech() = Unit
                override fun onError(error: Int) {
                    onHata(
                        when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "Konuşma algılanmadı. Tekrar deneyin."
                            SpeechRecognizer.ERROR_AUDIO -> "Mikrofon verisi alınamadı."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Mikrofon izni gerekli."
                            SpeechRecognizer.ERROR_NETWORK,
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Bu tanıma için ağ bağlantısı gerekli."
                            else -> "Konuşma tanınamadı. Tekrar deneyin veya metinle yazın."
                        },
                    )
                }
                override fun onResults(results: Bundle?) {
                    val sonuc = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()
                    if (sonuc.isBlank()) onHata("Konuşma algılanmadı. Tekrar deneyin.")
                    else onSonuc(sonuc)
                }
                override fun onPartialResults(partialResults: Bundle?) = Unit
                override fun onEvent(eventType: Int, params: Bundle?) = Unit
            },
        )
        runCatching { recognizer?.startListening(intent) }
            .onFailure { onHata("Dinleme başlatılamadı. Tekrar deneyin.") }
    }

    fun durdur() {
        runCatching { recognizer?.stopListening() }
    }

    fun serbestBirak() {
        recognizer?.destroy()
    }
}
