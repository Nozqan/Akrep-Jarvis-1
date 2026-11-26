package com.akrep.jarvis.ai.arayuz.gunes_hud

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun EnerjiHalkalari(
    renkler: List<Color>,
    aci: Float,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize().aspectRatio(1f)) {
        val merkez = Offset(size.width / 2f, size.height / 2f)
        renkler.forEachIndexed { indeks, renk ->
            val yaricap = size.minDimension * (0.34f + indeks * 0.08f)
            drawCircle(
                color = renk,
                radius = yaricap,
                center = merkez,
                style = Stroke(width = (2 + indeks).dp.toPx()),
            )
        }
    }
}
