package com.akrep.jarvis.ai.sistem_izleyici

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akrep.jarvis.ai.araclar.tema.CamYuzey

@Composable
fun SistemDurumuKartlari(durum: SistemDurumu) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CamYuzey.copy(alpha = 0.72f),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            listOf(
                "Batarya" to durum.batarya,
                "Batarya sıcaklığı" to durum.bataryaSicakligi,
                "Depolama" to durum.depolama,
                "Bellek" to durum.bellek,
                "Ağ" to durum.ag,
                "Ağ gecikmesi" to durum.agHizi,
                "Mikrofon" to durum.mikrofon,
                "Kamera" to durum.kamera,
                "Yapay zekâ" to durum.yapayZeka,
            ).forEach { (etiket, deger) ->
                Text(
                    text = "$etiket: $deger",
                    color = if (deger == "Çevrimdışı") Color(0xFFFFB547) else Color(0xFFA9B3C7),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
