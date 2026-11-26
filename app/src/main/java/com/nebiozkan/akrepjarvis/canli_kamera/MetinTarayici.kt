package com.nebiozkan.akrepjarvis.canli_kamera

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class MetinTarayici {
    private val taniyici = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun tara(giris: InputImage): Result<String> =
        suspendCancellableCoroutine { devam ->
            taniyici.process(giris)
                .addOnSuccessListener { devam.resume(Result.success(it.text)) }
                .addOnFailureListener { devam.resume(Result.failure(it)) }
        }

    fun serbestBirak() {
        taniyici.close()
    }
}
