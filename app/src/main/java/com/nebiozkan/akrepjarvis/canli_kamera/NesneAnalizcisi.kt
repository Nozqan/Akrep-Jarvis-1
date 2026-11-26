package com.nebiozkan.akrepjarvis.canli_kamera

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class NesneAnalizcisi {
    private val nesneTanima = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build(),
    )

    suspend fun analizEt(giris: InputImage): List<String> =
        suspendCancellableCoroutine { devam ->
            nesneTanima.process(giris)
                .addOnSuccessListener { nesneler ->
                    devam.resume(nesneler.distinctEtiketler())
                }
                .addOnFailureListener {
                    devam.resume(emptyList())
                }
        }

    fun serbestBirak() {
        nesneTanima.close()
    }

    private fun List<DetectedObject>.distinctEtiketler(): List<String> =
        flatMap { nesne ->
            nesne.labels
                .filter { it.confidence >= 0.45f }
                .map { it.text }
        }.distinct().take(5)
}
