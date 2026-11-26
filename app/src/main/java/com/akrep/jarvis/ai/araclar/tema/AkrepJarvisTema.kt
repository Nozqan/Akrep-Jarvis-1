package com.akrep.jarvis.ai.araclar.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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

private val AkrepSekilleri = Shapes(
    extraSmall = RoundedCornerShape(16),
    small = RoundedCornerShape(20),
    medium = RoundedCornerShape(28),
    large = RoundedCornerShape(36),
    extraLarge = RoundedCornerShape(48),
)

private val AkrepYaziTipi = Typography(
    headlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
)

@Composable
fun AkrepJarvisTema(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AkrepRenkler,
        shapes = AkrepSekilleri,
        typography = AkrepYaziTipi,
        content = content,
    )
}
