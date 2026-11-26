package com.nebiozkan.akrepjarvis.canli_kamera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nebiozkan.akrepjarvis.ortak.bilesenler.OvalCamPanel
import com.nebiozkan.akrepjarvis.ortak.tema.Altin
import com.nebiozkan.akrepjarvis.ortak.tema.BilgiMavi
import com.nebiozkan.akrepjarvis.ortak.tema.Hata
import com.nebiozkan.akrepjarvis.ortak.tema.UzaySiyahi
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class KameraDurumu(val etiket: String) {
    KAPALI("Kamera kapalı"),
    IZIN_BEKLENIyor("İzin bekleniyor"),
    HAZIRLANIyor("Tarama hazırlanıyor"),
    METIN_ANALIZ("Metin analiz ediliyor"),
    NESNE_ANALIZ("Nesne tanıma için destek kontrolü"),
    SONUC_HAZIR("Sonuç hazır"),
    BASARISIZ("İşlem başarısız"),
}

@Composable
fun KameraEkrani(onKapat: () -> Unit) {
    val context = LocalContext.current
    var izinVerildi by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED,
        )
    }
    val izinReddetti by remember { mutableStateOf(false) }
    var durum by remember { mutableStateOf(KameraDurumu.KAPALI) }
    var ocrSonucu by remember { mutableStateOf("") }
    var nesneSonucu by remember { mutableStateOf("") }
    var arananDosya by remember { mutableStateOf("") }

    val izinLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { verildi ->
        izinVerildi = verildi
        durum = if (verildi) KameraDurumu.HAZIRLANIyor else KameraDurumu.IZIN_BEKLENIyor
    }

    LaunchedEffect(izinVerildi) {
        if (!izinVerildi) {
            durum = KameraDurumu.IZIN_BEKLENIyor
            izinLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(UzaySiyahi)) {
        if (izinVerildi) {
            KameraOnizleme(
                modifier = Modifier.fillMaxSize(),
                onDurum = { durum = it },
                onOcrSonuc = { metin ->
                    ocrSonucu = metin
                    if (metin.isNotBlank()) {
                        durum = KameraDurumu.SONUC_HAZIR
                    }
                },
                onNesneSonuc = { nesneler ->
                    nesneSonucu = nesneler.joinToString(", ").ifBlank { "Nesne algılanmadı." }
                },
            )

            TaramaKatmani(durum = durum)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(onClick = onKapat) { Text("Kapat") }
                    Text(
                        text = durum.etiket,
                        color = if (durum == KameraDurumu.BASARISIZ) Hata else BilgiMavi,
                        fontWeight = FontWeight.Bold,
                    )
                }

                OvalCamPanel(
                    modifier = Modifier.padding(bottom = 18.dp),
                ) {
                    Text(
                        text = "Tarama sonucu",
                        color = Altin,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ocrSonucu.ifBlank { "Metin bekleniyor." },
                        color = Color(0xFFF7F8FC),
                        fontSize = 13.sp,
                        maxLines = 6,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Nesneler: $nesneSonucu",
                        color = BilgiMavi,
                        fontSize = 12.sp,
                        maxLines = 3,
                    )
                    if (ocrSonucu.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                arananDosya = taramaArsivle(context, ocrSonucu)
                            },
                            shape = RoundedCornerShape(22.dp),
                        ) {
                            Text("Sonucu arşivle")
                        }
                        if (arananDosya.isNotBlank()) {
                            Text(
                                text = "Arşiv: $arananDosya",
                                color = Color(0xFFA9B3C7),
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OvalCamPanel {
                    Text(
                        text = "Kamera izni gerekli",
                        color = Altin,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Canlı tarama, metin okuma ve OCR işlemleri için kamera kullanılmalı.",
                        color = Color(0xFFA9B3C7),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:${context.packageName}"),
                            )
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(22.dp),
                    ) {
                        Text("Uygulama ayarlarına git")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onKapat,
                        shape = RoundedCornerShape(22.dp),
                    ) {
                        Text("Geri dön")
                    }
                }
            }
        }
    }
}

@Composable
private fun KameraOnizleme(
    modifier: Modifier,
    onDurum: (KameraDurumu) -> Unit,
    onOcrSonuc: (String) -> Unit,
    onNesneSonuc: (List<String>) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val yuzIslemci = remember { Executors.newSingleThreadExecutor() }
    val taniyici = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    val nesneAnalizcisi = remember { NesneAnalizcisi() }
    val analizKapsami = remember { CoroutineScope(Dispatchers.Default) }
    var analizAktif by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose {
            analizAktif = false
            taniyici.close()
            nesneAnalizcisi.serbestBirak()
            yuzIslemci.shutdown()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { androidContext ->
            PreviewView(androidContext).apply {
                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        update = { previewView ->
            val providerGelecegi = ProcessCameraProvider.getInstance(context)
            providerGelecegi.addListener(
                {
                    runCatching {
                        val provider = providerGelecegi.get()
                        val preview = Preview.Builder().build().apply {
                            setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val analiz = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                            .build()
                        analiz.setAnalyzer(yuzIslemci) { goruntu ->
                            if (!analizAktif) {
                                goruntu.close()
                                return@setAnalyzer
                            }
                            val medyaGoruntu = goruntu.image
                            if (medyaGoruntu == null) {
                                goruntu.close()
                                return@setAnalyzer
                            }
                            onDurum(KameraDurumu.METIN_ANALIZ)
                            val giris = InputImage.fromMediaImage(
                                medyaGoruntu,
                                goruntu.imageInfo.rotationDegrees,
                            )
                            analizKapsami.launch {
                                val nesneler = nesneAnalizcisi.analizEt(giris)
                                onNesneSonuc(nesneler)
                            }
                            taniyici.process(giris)
                                .addOnSuccessListener { sonuc ->
                                    onOcrSonuc(sonuc.text)
                                }
                                .addOnFailureListener {
                                    onDurum(KameraDurumu.BASARISIZ)
                                }
                                .addOnCompleteListener {
                                    goruntu.close()
                                }
                        }
                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            analiz,
                        )
                        onDurum(KameraDurumu.SONUC_HAZIR)
                    }.onFailure {
                        onDurum(KameraDurumu.BASARISIZ)
                    }
                },
                ContextCompat.getMainExecutor(context),
            )
        },
    )
}

@Composable
private fun TaramaKatmani(durum: KameraDurumu) {
    val sonsuz = rememberInfiniteTransition(label = "tarama")
    val y by sonsuz.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = LinearEasing),
            RepeatMode.Reverse,
        ),
        label = "tarama_cizgisi",
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val sol = size.width * 0.12f
        val ust = size.height * 0.18f
        val sag = size.width * 0.88f
        val alt = size.height * 0.68f
        val koyuGrid = Color(0xFF67C7FF).copy(alpha = 0.14f)
        for (i in 1 until 8) {
            val x = sol + (sag - sol) * i / 8f
            drawLine(koyuGrid, Offset(x, ust), Offset(x, alt), 1.dp.toPx())
        }
        for (i in 1 until 8) {
            val yGrid = ust + (alt - ust) * i / 8f
            drawLine(koyuGrid, Offset(sol, yGrid), Offset(sag, yGrid), 1.dp.toPx())
        }

        val cap = 44.dp.toPx()
        listOf(
            Offset(sol, ust),
            Offset(sag, ust),
            Offset(sol, alt),
            Offset(sag, alt),
        ).forEachIndexed { indeks, nokta ->
            val yonX = if (indeks % 2 == 0) 1f else -1f
            val yonY = if (indeks < 2) 1f else -1f
            drawLine(
                Altin,
                nokta,
                Offset(nokta.x + yonX * cap, nokta.y),
                3.dp.toPx(),
                StrokeCap.Round,
            )
            drawLine(
                Altin,
                nokta,
                Offset(nokta.x, nokta.y + yonY * cap),
                3.dp.toPx(),
                StrokeCap.Round,
            )
        }

        val taramaY = ust + (alt - ust) * y
        drawLine(
            if (durum == KameraDurumu.BASARISIZ) Hata else BilgiMavi,
            Offset(sol, taramaY),
            Offset(sag, taramaY),
            2.dp.toPx(),
            StrokeCap.Round,
        )
    }
}

private fun taramaArsivle(context: Context, metin: String): String {
    val dosyaAdi = "tarama_" +
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale("tr", "TR")).format(Date()) +
        ".txt"
    val dosya = File(context.filesDir, dosyaAdi)
    runCatching {
        dosya.writeText(
            "AKREP JARVIS tarama sonucu\n" +
                "Tarih: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale("tr", "TR")).format(Date())}\n\n" +
                metin,
        )
    }.onFailure {
        return "Arşivlenemedi"
    }
    return dosya.name
}
