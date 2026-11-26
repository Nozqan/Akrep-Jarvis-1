package com.akrep.jarvis.ai.arayuz.gunes_hud

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.araclar.tema.BilgiMavi
import com.akrep.jarvis.ai.araclar.tema.EnerjiKirmizi
import com.akrep.jarvis.ai.araclar.tema.EnerjiYesil
import com.akrep.jarvis.ai.araclar.tema.GunesSari
import com.akrep.jarvis.ai.araclar.tema.UzaySiyahi

enum class HudDurumu {
    HAZIR,
    DINLIYOR,
    DUSUNUYOR,
    KONUSUYOR,
    HATA,
    CEVRIMDISI,
}

@Composable
fun GunesHud(
    durum: HudDurumu,
    modifier: Modifier = Modifier,
    genlik: Float = 0f,
    hareketAzaltma: Boolean = false,
) {
    val sonsuz = rememberInfiniteTransition(label = "solar_hud")
    val aci by sonsuz.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(
                if (hareketAzaltma) 6500 else when (durum) {
                    HudDurumu.DINLIYOR -> 900
                    HudDurumu.DUSUNUYOR -> 1300
                    HudDurumu.KONUSUYOR -> 700
                    HudDurumu.HATA -> 600
                    else -> 3000
                },
                easing = LinearEasing,
            ),
            RepeatMode.Restart,
        ),
        label = "halkalar",
    )
    val nabiz by sonsuz.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            tween(if (hareketAzaltma) 3000 else 850, easing = FastOutSlowInEasing),
            RepeatMode.Reverse,
        ),
        label = "cekirdek",
    )

    val vurgu = when (durum) {
        HudDurumu.DINLIYOR -> EnerjiYesil
        HudDurumu.DUSUNUYOR -> GunesSari
        HudDurumu.KONUSUYOR -> EnerjiKirmizi
        HudDurumu.HATA -> Color(0xFFFF5E6C)
        HudDurumu.CEVRIMDISI -> BilgiMavi
        HudDurumu.HAZIR -> Altin
    }

    Box(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val merkez = Offset(size.width / 2f, size.height / 2f)
            val min = size.minDimension

            drawCircle(
                color = vurgu.copy(alpha = 0.07f),
                radius = min * 0.47f,
                center = merkez,
            )
            drawCircle(
                color = vurgu.copy(alpha = 0.22f),
                radius = min * 0.47f,
                center = merkez,
                style = Stroke(width = 1.dp.toPx()),
            )

            listOf(
                EnerjiYesil to 0f,
                GunesSari to 110f,
                EnerjiKirmizi to 220f,
            ).forEachIndexed { indeks, (renk, kayma) ->
                val yaricap = min * (0.34f + indeks * 0.045f)
                drawOval(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            renk.copy(alpha = 0.18f),
                            renk,
                            Color.Transparent,
                            renk.copy(alpha = 0.55f),
                            renk.copy(alpha = 0.18f),
                        ),
                        center = merkez,
                    ),
                    topLeft = Offset(merkez.x - yaricap, merkez.y - yaricap * 0.72f),
                    size = Size(yaricap * 2f, yaricap * 1.44f),
                    style = Stroke(width = (3.2f - indeks * 0.45f).dp.toPx()),
                )

                val aciParca = Math.toRadians((aci + kayma).toDouble())
                val parcax = merkez.x + kotlin.math.cos(aciParca).toFloat() * yaricap
                val parcay = merkez.y + kotlin.math.sin(aciParca).toFloat() * yaricap * 0.72f
                if (!hareketAzaltma) {
                    drawCircle(
                        color = renk,
                        radius = (4.5f - indeks * 0.7f).dp.toPx(),
                        center = Offset(parcax, parcay),
                    )
                }
            }

            val cekirdekBuyume = if (durum == HudDurumu.DUSUNUYOR) nabiz else 1f + genlik.coerceIn(0f, 1f) * 0.12f
            val cekirdek = min * 0.19f * cekirdekBuyume
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        GunesSari,
                        vurgu,
                        EnerjiKirmizi.copy(alpha = 0.55f),
                        UzaySiyahi,
                    ),
                    center = Offset(merkez.x - cekirdek * 0.22f, merkez.y - cekirdek * 0.22f),
                    radius = cekirdek * 1.45f,
                ),
                radius = cekirdek,
                center = merkez,
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.72f),
                radius = cekirdek * 0.12f,
                center = Offset(merkez.x - cekirdek * 0.36f, merkez.y - cekirdek * 0.38f),
            )
        }
    }
}
