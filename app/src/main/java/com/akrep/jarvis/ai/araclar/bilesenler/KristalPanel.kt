package com.akrep.jarvis.ai.araclar.bilesenler

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.araclar.tema.CamYuzey

@Composable
fun KristalPanel(
    modifier: Modifier = Modifier,
    vurgu: Color = Altin,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = vurgu.copy(alpha = 0.10f),
                spotColor = vurgu.copy(alpha = 0.18f),
            ),
        shape = RoundedCornerShape(32.dp),
        color = CamYuzey.copy(alpha = 0.82f),
        border = BorderStroke(1.dp, vurgu.copy(alpha = 0.24f)),
    ) {
        Box(
            modifier = Modifier.background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.055f), Color.Transparent),
                ),
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content,
            )
        }
    }
}
