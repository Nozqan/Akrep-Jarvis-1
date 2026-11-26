package com.akrep.jarvis.ai.canli_gorus.canli_kamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.akrep.jarvis.ai.araclar.bilesenler.AkrepButon
import com.akrep.jarvis.ai.araclar.bilesenler.KristalPanel
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.araclar.tema.BilgiMavi
import com.akrep.jarvis.ai.araclar.tema.EnerjiYesil
import com.akrep.jarvis.ai.araclar.tema.GunesSari
import com.akrep.jarvis.ai.araclar.tema.UzaySiyahi
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

enum class KameraDurumu(val etiket: String) {
    KAPALI("Kamera kapalı"),
    IZIN_BEKLENIyor("İzin bekleniyor"),
    HAZIRLANIyor("Tarama hazırlanıyor"),
    METIN_ANALIZ("Metin analiz ediliyor"),
    NESNE_ANALIZ("Nesne analizi"),
    SONUC_HAZIR("Sonuç hazır"),
    BASARISIZ("İşlem başarısız"),
}

@Composable
fun KameraEkrani(onKapat: () -> Unit) {
    val context = LocalContext.current
    var izinVerildi by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    var durum by remember { mutableStateOf(KameraDurumu.KAPALI) }
    var ocrMetni by remember { mutableStateOf("") }
    var arananDosya by remember { mutableStateOf("") }

    val izinLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { verildi ->
        izinVerildi = verildi
        durum = if (verildi) KameraDurumu.HAZIRLANIyor else KameraDurumu.IZIN_BEKLENIyor
    }

    LaunchedEffect(Unit) {
        if (!izinVerildi) {
            durum = KameraDurumu.IZIN_BEKLENIyor
            izinLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UzaySiyahi)
            .statusBarsPadding(),
    ) {
        if (izinVerildi) {
            CanliOnizleme(
                modifier = Modifier.fillMaxSize(),
                onDurum = { durum = it },
                onMetin = {
                    ocrMetni = it
                    if (it.isNotBlank()) durum = KameraDurumu.SONUC_HAZIR
                },
            )

            PremiumTaramaKatmani(durum)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AkrepButon(
                        onClick = onKapat,
                        modifier = Modifier.weight(0.42f),
                        vurgu = Color(0xFFA9B3C7),
                    ) { Text("Kapat") }

                    KristalPanel(modifier = Modifier.weight(0.58f), vurgu = BilgiMavi) {
                        Text(
                            text = durum.etiket.uppercase(),
                            color = if (durum == KameraDurumu.BASARISIZ) Color(0xFFFF5E6C) else BilgiMavi,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                KristalPanel(vurgu = GunesSari) {
                    Text(
                        text = "CANLI GÖRÜŞ",
                        color = GunesSari,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ocrMetni.ifBlank { "Çevre taranıyor. Metin ve nesneler algılanıyor." },
                        color = Color(0xFFF7F8FC),
                        fontSize = 13.sp,
                        maxLines = 5,
                    )
                    if (ocrMetni.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        AkrepButon(
                            onClick = {
                                arananDosya = TaramaArsivi(context).kaydet(ocrMetni)
                                    .fold(onSuccess = { it.name }, onFailure = { "Arşivlenemedi" })
                            },
                            modifier = Modifier.fillMaxWidth(),
                            vurgu = EnerjiYesil,
                        ) { Text("Sonucu Arşivle") }
                        if (arananDosya.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = arananDosya,
                                color = Color(0xFFA9B3C7),
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                KristalPanel(vurgu = Altin) {
                    Text(
                        text = "Kamera İzni Gerekli",
                        color = Altin,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Canlı görüş ve OCR tarama için kamera erişimi gereklidir.",
                        color = Color(0xFFA9B3C7),
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    AkrepButon(
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:${context.packageName}"),
                            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Sistem Ayarlarına Git") }
                    Spacer(modifier = Modifier.height(8.dp))
                    AkrepButon(
                        onClick = onKapat,
                        modifier = Modifier.fillMaxWidth(),
                        vurgu = Color(0xFFA9B3C7),
                    ) { Text("Geri Dön") }
                }
            }
        }
    }
}

@Composable
private fun CanliOnizleme(
    modifier: Modifier,
    onDurum: (KameraDurumu) -> Unit,
    onMetin: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    val metinTarayici = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    var aktif by remember { mutableStateOf(true) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            aktif = false
            metinTarayici.close()
            executor.shutdown()
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
            val providerFuture = ProcessCameraProvider.getInstance(context)
            providerFuture.addListener(
                {
                    runCatching {
                        val provider = providerFuture.get()
                        val preview = Preview.Builder().build().apply {
                            setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val analiz = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        analiz.setAnalyzer(executor) { goruntu ->
                            if (!aktif) {
                                goruntu.close()
                                return@setAnalyzer
                            }
                            val goruntuRef = goruntu.image
                            if (goruntuRef == null) {
                                goruntu.close()
                                return@setAnalyzer
                            }
                            onDurum(KameraDurumu.METIN_ANALIZ)
                            val input = InputImage.fromMediaImage(
                                goruntuRef,
                                goruntu.imageInfo.rotationDegrees,
                            )
                            metinTarayici.process(input)
                                .addOnSuccessListener { sonuc -> onMetin(sonuc.text) }
                                .addOnFailureListener { onDurum(KameraDurumu.BASARISIZ) }
                                .addOnCompleteListener { goruntu.close() }
                        }
                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            analiz,
                        )
                        onDurum(KameraDurumu.SONUC_HAZIR)
                    }.onFailure { onDurum(KameraDurumu.BASARISIZ) }
                },
                ContextCompat.getMainExecutor(context),
            )
        },
    )
}

@Composable
private fun PremiumTaramaKatmani(durum: KameraDurumu) {
    val sonsuz = rememberInfiniteTransition(label = "premium_tarama")
    val ilerleme by sonsuz.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = LinearEasing),
            RepeatMode.Reverse,
        ),
        label = "tarama_ilerleme",
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val sol = size.width * 0.12f
        val sag = size.width * 0.88f
        val ust = size.height * 0.16f
        val alt = size.height * 0.66f

        drawRect(
            brush = Brush.verticalGradient(
                listOf(Color.Transparent, Color(0xFF67C7FF).copy(alpha = 0.05f), Color.Transparent),
            ),
        )

        val gridRenk = BilgiMavi.copy(alpha = 0.14f)
        for (i in 1..8) {
            val x = sol + (sag - sol) * i / 9f
            drawLine(gridRenk, Offset(x, ust), Offset(x, alt), 1.dp.toPx())
        }
        for (i in 1..6) {
            val y = ust + (alt - ust) * i / 7f
            drawLine(gridRenk, Offset(sol, y), Offset(sag, y), 1.dp.toPx())
        }

        val y = ust + (alt - ust) * ilerleme
        val taramaRenk = if (durum == KameraDurumu.BASARISIZ) Color(0xFFFF5E6C) else EnerjiYesil
        drawLine(taramaRenk, Offset(sol, y), Offset(sag, y), 2.dp.toPx())

        val koseler = listOf(
            Offset(sol, ust),
            Offset(sag, ust),
            Offset(sol, alt),
            Offset(sag, alt),
        )
        koseler.forEachIndexed { indeks, nokta ->
            val xYon = if (indeks % 2 == 0) 1f else -1f
            val yYon = if (indeks < 2) 1f else -1f
            drawLine(Altin, nokta, Offset(nokta.x + xYon * 48.dp.toPx(), nokta.y), 3.dp.toPx())
            drawLine(Altin, nokta, Offset(nokta.x, nokta.y + yYon * 48.dp.toPx()), 3.dp.toPx())
        }

        val hedef = Offset(size.width / 2f, size.height * 0.46f)
        drawCircle(
            color = GunesSari.copy(alpha = 0.72f),
            radius = 28.dp.toPx(),
            center = hedef,
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}
