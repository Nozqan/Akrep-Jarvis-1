package com.akrep.jarvis.ai.arayuz.giris_ekrani

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akrep.jarvis.ai.ayarlar.Ayarlar
import com.akrep.jarvis.ai.ayarlar.AyarlarDeposu
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.araclar.tema.BilgiMavi
import com.akrep.jarvis.ai.araclar.tema.EnerjiKirmizi
import com.akrep.jarvis.ai.araclar.tema.EnerjiYesil
import com.akrep.jarvis.ai.araclar.tema.GunesSari
import com.akrep.jarvis.ai.araclar.tema.UzaySiyahi
import kotlinx.coroutines.delay

@Composable
fun GirisEkrani(onGirisTamamlandi: () -> Unit) {
    val context = LocalContext.current
    val ayarlarDeposu = remember { AyarlarDeposu(context) }
    val ayarlar by ayarlarDeposu.ayarlar.collectAsState(initial = Ayarlar())
    var asama by remember { mutableStateOf(GirisDurumu.ACILIyor) }
    var metinGorunurlugu by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val sure = GirisAnimasyonlari.sureMilisaniye(ayarlar.tamGirisAnimasyonu)
        delay(sure[0])
        asama = GirisDurumu.SISTEM_KONTROLU
        delay(sure[1])
        asama = GirisDurumu.HAZIR
        delay(sure[2])
        onGirisTamamlandi()
    }

    LaunchedEffect(Unit) {
        delay(350)
        metinGorunurlugu = 1f
    }

    val sonsuz = rememberInfiniteTransition(label = "solar_splash")
    val aci by sonsuz.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(if (ayarlar.hareketAzaltma) 5000 else 2200, easing = LinearEasing),
            RepeatMode.Restart,
        ),
        label = "halkalar",
    )
    val cekirdekNabizi by sonsuz.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            tween(if (ayarlar.hareketAzaltma) 2600 else 900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse,
        ),
        label = "cekirdek_nabzi",
    )
    val metinAlfa by animateFloatAsState(
        targetValue = metinGorunurlugu,
        animationSpec = tween(900),
        label = "metin_gorunurlugu",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF18243B),
                        Color(0xFF0B1224),
                        UzaySiyahi,
                    ),
                    radius = 900f,
                ),
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridAralik = size.width / 12f
            val vurgu = BilgiMavi.copy(alpha = 0.07f)
            for (x in 0..12) {
                val cx = gridAralik * x
                drawLine(vurgu, Offset(cx, 0f), Offset(cx, size.height), 1.dp.toPx())
            }
            for (y in 0..20) {
                val cy = size.height / 20f * y
                drawLine(vurgu, Offset(0f, cy), Offset(size.width, cy), 1.dp.toPx())
            }
            for (i in 0 until 62) {
                val x = ((i * 137) % 997) / 997f * size.width
                val y = ((i * 83) % 991) / 991f * size.height
                val parilti = 0.12f + (i % 5) * 0.08f
                drawCircle(
                    color = if (i % 11 == 0) GunesSari.copy(alpha = parilti) else Color.White.copy(alpha = parilti),
                    radius = if (i % 13 == 0) 1.8.dp.toPx() else 0.8.dp.toPx(),
                    center = Offset(x, y),
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val merkez = Offset(size.width / 2f, size.height / 2f)
                    val min = size.minDimension

                    drawCircle(
                        color = Altin.copy(alpha = 0.10f),
                        radius = min * 0.43f,
                        center = merkez,
                        style = Stroke(width = 2.dp.toPx()),
                    )

                    listOf(
                        EnerjiYesil to 0f,
                        GunesSari to 115f,
                        EnerjiKirmizi to 230f,
                    ).forEachIndexed { indeks, (renk, kayma) ->
                        val yaricap = min * (0.31f + indeks * 0.055f)
                        drawCircle(
                            color = renk.copy(alpha = 0.32f),
                            radius = yaricap,
                            center = merkez,
                            style = Stroke(width = (2.5f + indeks).dp.toPx()),
                        )
                        drawArc(
                            color = renk,
                            startAngle = aci + kayma,
                            sweepAngle = 92f,
                            useCenter = false,
                            style = Stroke(
                                width = (4f - indeks * 0.45f).dp.toPx(),
                            ),
                            topLeft = Offset(merkez.x - yaricap, merkez.y - yaricap),
                            size = androidx.compose.ui.geometry.Size(yaricap * 2f, yaricap * 2f),
                        )
                    }

                    val cekirdek = min * 0.16f * cekirdekNabizi
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                GunesSari,
                                Altin,
                                EnerjiKirmizi.copy(alpha = 0.62f),
                                Color.Transparent,
                            ),
                            center = merkez,
                            radius = cekirdek * 1.35f,
                        ),
                        radius = cekirdek,
                        center = merkez,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "AKREP JARVIS",
                    color = Altin,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Geliştirici: Nebi Özkan / AKREP",
                    color = GunesSari,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = asama.etiket.uppercase(),
                    color = when (asama) {
                        GirisDurumu.HATA -> Color(0xFFFF5E6C)
                        GirisDurumu.KISITLI_MOD -> BilgiMavi
                        else -> Color(0xFFA9B3C7)
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }
        }
    }
}
