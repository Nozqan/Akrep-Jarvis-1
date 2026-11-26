package com.akrep.jarvis.ai.canli_gorus.canli_kamera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executor

class KameraYoneticisi(private val context: Context) {
    fun bagla(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        analizExecutor: Executor,
        goruntuAnalizi: (androidx.camera.core.ImageProxy) -> Unit,
        onHazir: () -> Unit,
        onHata: (String) -> Unit,
    ) {
        val providerGelecegi = ProcessCameraProvider.getInstance(context)
        providerGelecegi.addListener(
            {
                runCatching {
                    val provider = providerGelecegi.get()
                    val preview = Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .build()
                    imageAnalysis.setAnalyzer(analizExecutor) { goruntuAnalizi(it) }
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis,
                    )
                    onHazir()
                }.onFailure {
                    onHata(it.message ?: "Kamera başlatılamadı.")
                }
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    fun serbestBirak() {
        runCatching {
            ProcessCameraProvider.getInstance(context).get().unbindAll()
        }
    }
}
