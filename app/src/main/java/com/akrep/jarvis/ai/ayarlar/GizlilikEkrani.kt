package com.akrep.jarvis.ai.ayarlar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akrep.jarvis.ai.araclar.bilesenler.AkrepButon
import com.akrep.jarvis.ai.araclar.bilesenler.KristalPanel
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.araclar.tema.BilgiMavi
import com.akrep.jarvis.ai.araclar.tema.EnerjiYesil
import com.akrep.jarvis.ai.araclar.tema.GunesSari
import com.akrep.jarvis.ai.araclar.tema.UzaySiyahi
import com.akrep.jarvis.ai.canli_gorus.canli_kamera.TaramaArsivi
import com.akrep.jarvis.ai.veri.depo.NotYoneticisi
import com.akrep.jarvis.ai.veri.depo.SohbetGecmisiYoneticisi
import com.akrep.jarvis.ai.yapay_zeka.KomutYonlendirici

@Composable
fun GizlilikEkrani(onGeri: () -> Unit) {
    val context = LocalContext.current
    val sonuc = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Brush.verticalGradient(listOf(Color(0xFF101B33), UzaySiyahi))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AkrepButon(
                    onClick = onGeri,
                    modifier = Modifier.fillMaxWidth(0.28f),
                    vurgu = Color(0xFFA9B3C7),
                ) { Text("Geri") }
                KristalPanel(modifier = Modifier.fillMaxWidth(0.68f), vurgu = Altin) {
                    Text("GİZLİLİK MERKEZİ", color = Altin, fontWeight = FontWeight.Bold)
                }
            }

            KristalPanel(vurgu = EnerjiYesil) {
                Text("KAMERA VE MİKROFON", color = EnerjiYesil, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                GizlilikMetni("Kamera yalnızca Canlı Görüş ekranı açıkken kullanılır.")
                GizlilikMetni("Mikrofon yalnızca Dinle düğmesine basıldığında aktif edilir.")
                GizlilikMetni("Ses verileri varsayılan olarak kalıcı kaydedilmez.")
            }

            KristalPanel(vurgu = GunesSari) {
                Text("VERİ AKIŞI", color = GunesSari, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                GizlilikMetni("Yerel komutlar internete gönderilmez.")
                GizlilikMetni("Harici yapay zekaya veri yalnızca açık kullanıcı izniyle gönderilir.")
                GizlilikMetni("API anahtarları Android Keystore tabanlı şifreli depoda tutulur.")
            }

            KristalPanel(vurgu = BilgiMavi) {
                Text("GEÇMİŞ VE ARŞİV", color = BilgiMavi, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                GizlilikMetni("Notlar, sohbet geçmişi ve OCR tarama dosyaları cihaz üzerinde tutulur.")
                Spacer(modifier = Modifier.height(10.dp))
                AkrepButon(
                    onClick = {
                        val notYoneticisi = NotYoneticisi(context)
                        notYoneticisi.hepsi().forEach { notYoneticisi.sil(it.id) }
                        SohbetGecmisiYoneticisi(context).temizle()
                        TaramaArsivi(context).temizle()
                        val yonlendirici = KomutYonlendirici(context)
                        yonlendirici.apiAnahtariSil("Gemini")
                        yonlendirici.apiAnahtariSil("OpenAI")
                        sonuc.value = "Notlar, sohbet geçmişi, tarama arşivleri ve API anahtarları silindi."
                    },
                    modifier = Modifier.fillMaxWidth(),
                    vurgu = Color(0xFFFF5E6C),
                ) { Text("Tüm Geçmişi Sil") }
                if (sonuc.value.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(sonuc.value, color = Color(0xFFA9B3C7), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun GizlilikMetni(metin: String) {
    Text(
        text = "• $metin",
        color = Color(0xFFF7F8FC),
        fontSize = 14.sp,
        modifier = Modifier.padding(vertical = 3.dp),
    )
}
