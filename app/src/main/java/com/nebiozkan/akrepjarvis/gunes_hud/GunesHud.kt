package com.nebiozkan.akrepjarvis.gunes_hud

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.nebiozkan.akrepjarvis.ortak.tema.Altin
import com.nebiozkan.akrepjarvis.ortak.tema.BilgiMavi
import com.nebiozkan.akrepjarvis.ortak.tema.EnerjiKirmizi
import com.nebiozkan.akrepjarvis.ortak.tema.EnerjiYesil
import com.nebiozkan.akrepjarvis.ortak.tema.GunesSari
import com.nebiozkan.akrepjarvis.ortak.tema.UzaySiyahi

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
) {
    val sonsuz = rememberInfiniteTransition(label = "gunes_hud")
    val aci by sonsuz.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = when (durum) {
                    HudDurumu.DINLIYOR -> 900
                    HudDurumu.DUSUNUYOR -> 1300
                    HudDurumu.KONUSUYOR -> 700
                    HudDurumu.HATA -> 180
                    else -> 2400
                },
                easing = LinearEasing,
            ),
            RepeatMode.Restart,
        ),
        label = "hud_donme",
    )
    val nabiz = 1f + genlik.coerceIn(0f, 1f) * 0.10f

    Box(
        modifier = modifier.fillMaxWidth(0.78f).aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val merkez = Offset(size.width / 2f, size.height / 2f)
            val cekirdek = size.minDimension * 0.22f * nabiz
            val baskin = when (durum) {
                HudDurumu.DINLIYOR -> EnerjiYesil
                HudDurumu.DUSUNUYOR -> GunesSari
                HudDurumu.KONUSUYOR -> EnerjiKirmizi
                HudDurumu.HATA -> Color(0xFFFF5E6C)
                HudDurumu.CEVRIMDISI -> BilgiMavi
                HudDurumu.HAZIR -> Altin
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.9f), baskin, baskin.copy(alpha = 0.35f), UzaySiyahi),
                    center = merkez,
                    radius = cekirdek * 1.25f,
                ),
                radius = cekirdek,
                center = merkez,
            )

            listOf(
                EnerjiYesil,
                GunesSari,
                EnerjiKirmizi,
                if (durum == HudDurumu.CEVRIMDISI) BilgiMavi else baskin,
            ).forEachIndexed { indeks, renk ->
                drawCircle(
                    color = renk.copy(alpha = if (indeks == 3) 0.65f else 0.75f),
                    radius = size.minDimension * (0.33f + indeks * 0.075f),
                    center = merkez,
                    style = Stroke(width = (3 + indeks).dp.toPx()),
                )
                for (nokta in 0 until 3) {
                    val radyan = Math.toRadians((aci + indeks * 54f + nokta * 120f).toDouble())
                    val yaricap = size.minDimension * (0.33f + indeks * 0.075f)
                    val konum = Offset(
                        merkez.x + kotlin.math.cos(radyan).toFloat() * yaricap,
                        merkez.y + kotlin.math.sin(radyan).toFloat() * yaricap,
                    )
                    drawCircle(
                        color = renk,
                        radius = 3.dp.toPx(),
                        center = konum,
                    )
                }
            }
        }
    }
}
