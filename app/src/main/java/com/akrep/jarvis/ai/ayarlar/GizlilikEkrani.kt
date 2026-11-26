package com.akrep.jarvis.ai.ayarlar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akrep.jarvis.ai.araclar.bilesenler.OvalCamPanel
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.veri.depo.NotYoneticisi
import com.akrep.jarvis.ai.veri.depo.SohbetGecmisiYoneticisi
import com.akrep.jarvis.ai.canli_gorus.canli_kamera.TaramaArsivi
import java.io.File

@Composable
fun GizlilikEkrani(onGeri: () -> Unit) {
    val context = LocalContext.current
    val sonuc = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = onGeri, shape = RoundedCornerShape(22.dp)) { Text("Geri") }
            Text("Gizlilik", color = Altin, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        OvalCamPanel {
            Text(
                "Veri ve kamera kullanımı",
                color = Altin,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Kamera yalnızca kamera ekranı açıkken kullanılır. OCR işlemi öncelikle cihaz üzerinde yapılır.",
                color = Color(0xFFF7F8FC),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Mikrofon yalnızca “Dinle” düğmesine basıldığında aktif edilir. Ses verileri sürekli kaydedilmez.",
                color = Color(0xFFF7F8FC),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Yerel komutlar internete gönderilmez. Harici yapay zekâ yalnızca ayarlardan açık izin verilirse kullanılabilir.",
                color = Color(0xFFF7F8FC),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "API anahtarları Android Keystore tabanlı şifreli depoda tutulur; düz metin olarak kaydedilmez veya loglanmaz.",
                color = Color(0xFFF7F8FC),
            )
        }

        OvalCamPanel {
            Text("Geçmişi sil", color = Altin, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val notlar = NotYoneticisi(context)
                    notlar.hepsi().forEach { notlar.sil(it.id) }
                    SohbetGecmisiYoneticisi(context).temizle()
                    TaramaArsivi(context).temizle()
                    sonuc.value = "Notlar, sohbet geçmişi ve kamera tarama arşivleri silindi."
                },
                shape = RoundedCornerShape(22.dp),
            ) { Text("Notları ve tarama arşivini temizle") }
            if (sonuc.value.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(sonuc.value, color = Color(0xFFA9B3C7), fontSize = 12.sp)
            }
        }
    }
}
