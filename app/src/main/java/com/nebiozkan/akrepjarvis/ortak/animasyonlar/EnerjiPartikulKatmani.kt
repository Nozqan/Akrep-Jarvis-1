package com.nebiozkan.akrepjarvis.ortak.animasyonlar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun EnerjiPartikulKatmani(
    hareketli: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val miktar = if (hareketli) 36 else 12
        for (i in 0 until miktar) {
            val x = ((i * 73) % 997) / 997f * size.width
            val y = ((i * 41) % 991) / 991f * size.height
            drawCircle(
                color = Color(0xFFFFC928).copy(alpha = 0.20f),
                radius = if (i % 7 == 0) 2f else 1f,
                center = Offset(x, y),
            )
        }
    }
}
