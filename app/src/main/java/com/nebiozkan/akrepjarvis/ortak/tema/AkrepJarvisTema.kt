package com.nebiozkan.akrepjarvis.ortak.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val UzaySiyahi = Color(0xFF050812)
val DerinLacivert = Color(0xFF0B1224)
val CamYuzey = Color(0xFF172033)
val Altin = Color(0xFFFFC928)
val GunesSari = Color(0xFFFFE36A)
val EnerjiKirmizi = Color(0xFFFF4D38)
val EnerjiYesil = Color(0xFF54E38E)
val BilgiMavi = Color(0xFF67C7FF)
val AnaMetin = Color(0xFFF7F8FC)
val IkincilMetin = Color(0xFFA9B3C7)
val Uyari = Color(0xFFFFB547)
val Hata = Color(0xFFFF5E6C)

private val AkrepRenkler = darkColorScheme(
    primary = Altin,
    onPrimary = UzaySiyahi,
    secondary = EnerjiYesil,
    background = UzaySiyahi,
    surface = CamYuzey,
    onBackground = AnaMetin,
    onSurface = AnaMetin,
    error = Hata,
)

@Composable
fun AkrepJarvisTema(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AkrepRenkler,
        content = content,
    )
}
