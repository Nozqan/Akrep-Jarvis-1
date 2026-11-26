package com.nebiozkan.akrepjarvis.gunes_hud

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun SesGorsellestirici(
    genlik: Float,
    renk: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxWidth().height(28.dp)) {
        val orta = size.height / 2f
        val toplam = 24
        for (i in 0 until toplam) {
            val dalga = (kotlin.math.sin(i * 0.9f) * genlik).toFloat().coerceIn(-1f, 1f)
            val x = size.width * i / (toplam - 1f)
            val boy = (5f + kotlin.math.abs(dalga) * 16f).dp.toPx()
            drawLine(
                renk,
                Offset(x, orta - boy),
                Offset(x, orta + boy),
                2.dp.toPx(),
                StrokeCap.Round,
            )
        }
    }
}
