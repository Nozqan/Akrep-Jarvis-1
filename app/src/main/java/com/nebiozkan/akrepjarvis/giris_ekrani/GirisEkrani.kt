package com.nebiozkan.akrepjarvis.giris_ekrani

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebiozkan.akrepjarvis.ortak.tema.Altin
import com.nebiozkan.akrepjarvis.ortak.tema.EnerjiKirmizi
import com.nebiozkan.akrepjarvis.ortak.tema.EnerjiYesil
import com.nebiozkan.akrepjarvis.ortak.tema.GunesSari
import com.nebiozkan.akrepjarvis.ortak.tema.UzaySiyahi
import kotlinx.coroutines.delay
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.nebiozkan.akrepjarvis.ayarlar.Ayarlar
import com.nebiozkan.akrepjarvis.ayarlar.AyarlarDeposu

@Composable
fun GirisEkrani(onGirisTamamlandi: () -> Unit) {
    val context = LocalContext.current
    val ayarlarDeposu = remember { AyarlarDeposu(context) }
    val ayarlar by ayarlarDeposu.ayarlar.collectAsState(initial = Ayarlar())
    var adim by remember { mutableStateOf("Açılıyor") }
    LaunchedEffect(Unit) {
        val toplamSure = if (ayarlar.tamGirisAnimasyonu) 900L else 450L
        delay(toplamSure)
        adim = "Sistem kontrolü"
        delay(if (ayarlar.tamGirisAnimasyonu) 900L else 450L)
        adim = "Hazır"
        delay(if (ayarlar.tamGirisAnimasyonu) 700L else 350L)
        onGirisTamamlandi()
    }

    val sonsuz = rememberInfiniteTransition(label = "giris")
    val aci by sonsuz.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(2400, easing = LinearEasing),
            RepeatMode.Restart,
        ),
        label = "halkalar",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF172033), UzaySiyahi),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0 until 48) {
                val x = (i * 83 % 997) / 997f * size.width
                val y = (i * 47 % 991) / 991f * size.height
                val pariltama = 0.25f + ((i * 13) % 50) / 100f
                drawCircle(
                    color = Color.White.copy(alpha = pariltama),
                    radius = if (i % 9 == 0) 1.8.dp.toPx() else 1.dp.toPx(),
                    center = Offset(x, y),
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val merkez = Offset(size.width / 2f, size.height / 2f)
                    val yaricap = size.minDimension * 0.38f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(GunesSari, Altin, EnerjiKirmizi, UzaySiyahi),
                            center = merkez,
                            radius = yaricap,
                        ),
                        radius = yaricap,
                    )

                    listOf(
                        EnerjiYesil to 0f,
                        GunesSari to 120f,
                        EnerjiKirmizi to 240f,
                    ).forEachIndexed { katman, renkAci ->
                        val baslangic = aci + renkAci.second
                        drawArc(
                            color = renkAci.first,
                            startAngle = baslangic,
                            sweepAngle = 100f,
                            useCenter = false,
                            style = Stroke(
                                width = (5 - katman * 0.6f).dp.toPx(),
                                cap = StrokeCap.Round,
                            ),
                        )
                    }
                }
            }

            Text(
                text = "AKREP JARVIS",
                color = Altin,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 18.dp),
            )
            Text(
                text = "Geliştirici: Nebi Özkan",
                color = GunesSari,
                fontSize = 15.sp,
            )
            Text(
                text = adim,
                color = Color(0xFFA9B3C7),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
