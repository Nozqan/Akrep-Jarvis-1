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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akrep.jarvis.ai.araclar.bilesenler.AkrepButon
import com.akrep.jarvis.ai.araclar.bilesenler.KristalPanel
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.araclar.tema.BilgiMavi
import com.akrep.jarvis.ai.araclar.tema.EnerjiYesil
import com.akrep.jarvis.ai.araclar.tema.GunesSari
import com.akrep.jarvis.ai.araclar.tema.UzaySiyahi
import com.akrep.jarvis.ai.yapay_zeka.BaglantiDurumu
import com.akrep.jarvis.ai.yapay_zeka.KomutYonlendirici
import kotlinx.coroutines.launch

@Composable
fun AyarlarEkrani(onGeri: () -> Unit) {
    val context = LocalContext.current
    val depo = remember { AyarlarDeposu(context) }
    val yonlendirici = remember { KomutYonlendirici(context) }
    val ayarlar by depo.ayarlar.collectAsState(initial = Ayarlar())
    val islemKapsami = rememberCoroutineScope()
    var apiAnahtari by remember(ayarlar.saglayici) {
        mutableStateOf(yonlendirici.apiAnahtariOku(ayarlar.saglayici).orEmpty())
    }
    var baglantiMesaji by remember { mutableStateOf("") }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AkrepButon(
                    onClick = onGeri,
                    modifier = Modifier.fillMaxWidth(0.28f),
                    vurgu = Color(0xFFA9B3C7),
                ) { Text("Geri") }
                KristalPanel(modifier = Modifier.fillMaxWidth(0.68f), vurgu = Altin) {
                    Text("SİSTEM AYARLARI", color = Altin, fontWeight = FontWeight.Bold)
                }
            }

            KristalPanel(vurgu = GunesSari) {
                Text("YAPAY ZEKÂ MOTORU", color = GunesSari, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Yerel", "OpenAI", "Gemini").forEach { ad ->
                        AkrepButon(
                            onClick = {
                                islemKapsami.launch { depo.saglayiciAyarla(ad) }
                            },
                            modifier = Modifier.weight(1f),
                            vurgu = if (ayarlar.saglayici == ad) GunesSari else Color(0xFFA9B3C7),
                        ) { Text(ad) }
                    }
                }

                if (ayarlar.saglayici != "Yerel") {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = apiAnahtari,
                        onValueChange = { apiAnahtari = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        label = { Text("API anahtarı") },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GunesSari),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ayarlar.ucNokta,
                        onValueChange = { islemKapsami.launch { depo.ucNoktaAyarla(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        label = { Text("API uç noktası") },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ayarlar.model,
                        onValueChange = { islemKapsami.launch { depo.modelAyarla(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        label = { Text("Model") },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AkrepButon(
                            onClick = {
                                yonlendirici.apiAnahtariKaydet(ayarlar.saglayici, apiAnahtari)
                                baglantiMesaji = "Anahtar şifreli depoya kaydedildi."
                            },
                            modifier = Modifier.weight(1f),
                            vurgu = EnerjiYesil,
                        ) { Text("Kaydet") }
                        AkrepButon(
                            onClick = {
                                yonlendirici.apiAnahtariSil(ayarlar.saglayici)
                                apiAnahtari = ""
                                baglantiMesaji = "API anahtarı silindi."
                            },
                            modifier = Modifier.weight(1f),
                            vurgu = Color(0xFFA9B3C7),
                        ) { Text("Sil") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    AkrepButon(
                        onClick = {
                            islemKapsami.launch {
                                baglantiMesaji = when (val durum = yonlendirici.baglantiKontrolu()) {
                                    BaglantiDurumu.Bagli -> "Bağlantı başarılı."
                                    BaglantiDurumu.BagliDegil -> "Bağlantı yok."
                                    is BaglantiDurumu.Hata -> durum.mesaj
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        vurgu = BilgiMavi,
                    ) { Text("Bağlantıyı Test Et") }
                    if (baglantiMesaji.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(baglantiMesaji, color = Color(0xFFA9B3C7), fontSize = 12.sp)
                    }
                }
            }

            KristalPanel(vurgu = EnerjiYesil) {
                Text("KULLANICI DENETİMLERİ", color = EnerjiYesil, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                AyarAnahtari("Harici yapay zekaya veri gönder", ayarlar.hariciYapayZekaIzni) {
                    islemKapsami.launch { depo.hariciIzniAyarla(it) }
                }
                AyarAnahtari("Sesli yanıt", ayarlar.sesliYanıt) {
                    islemKapsami.launch { depo.sesliYanitiAyarla(it) }
                }
                AyarAnahtari("Tam giriş animasyonu", ayarlar.tamGirisAnimasyonu) {
                    islemKapsami.launch { depo.tamGirisAyarla(it) }
                }
                AyarAnahtari("Hareket azaltma", ayarlar.hareketAzaltma) {
                    islemKapsami.launch { depo.hareketAzaltmaAyarla(it) }
                }
            }
        }
    }
}

@Composable
private fun AyarAnahtari(metin: String, acik: Boolean, onDegisim: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(metin, color = Color(0xFFF7F8FC), modifier = Modifier.weight(1f))
        Switch(
            checked = acik,
            onCheckedChange = onDegisim,
            colors = SwitchDefaults.colors(
                checkedThumbColor = UzaySiyahi,
                checkedTrackColor = EnerjiYesil,
                uncheckedThumbColor = Color(0xFFA9B3C7),
                uncheckedTrackColor = CamRenk(),
            ),
        )
    }
}

@Composable
private fun CamRenk(): Color = Color(0xFF172033)
