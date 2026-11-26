package com.akrep.jarvis.ai.araclar.bilesenler

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.araclar.tema.UzaySiyahi

@Composable
fun AkrepButon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    vurgu: Color = Altin,
    icerik: @Composable RowScope.() -> Unit,
) {
    val etkilesim = remember { MutableInteractionSource() }
    val basili by etkilesim.collectIsPressedAsState()
    val olcek by animateFloatAsState(
        targetValue = if (basili) 0.94f else 1f,
        animationSpec = tween(160),
        label = "buton_olcegi",
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = 52.dp)
            .scale(olcek),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = vurgu,
            contentColor = UzaySiyahi,
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        interactionSource = etkilesim,
        content = icerik,
    )
}
