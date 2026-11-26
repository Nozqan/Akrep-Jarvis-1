package com.nebiozkan.akrepjarvis.ayarlar

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebiozkan.akrepjarvis.ortak.bilesenler.OvalCamPanel
import com.nebiozkan.akrepjarvis.ortak.tema.Altin
import com.nebiozkan.akrepjarvis.yapay_zeka.BaglantiDurumu
import com.nebiozkan.akrepjarvis.yapay_zeka.KomutYonlendirici
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = onGeri, shape = RoundedCornerShape(22.dp)) { Text("Geri") }
            Text("Ayarlar", color = Altin, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        OvalCamPanel {
            Text("Yapay zekâ sağlayıcısı", color = Altin, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("Yerel", "OpenAI", "Gemini").forEach { ad ->
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            islemKapsami.launch { depo.saglayiciAyarla(ad) }
                        },
                        shape = RoundedCornerShape(22.dp),
                    ) { Text(ad) }
                }
            }

            if (ayarlar.saglayici != "Yerel") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = apiAnahtari,
                    onValueChange = { apiAnahtari = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("API anahtarı") },
                    visualTransformation = PasswordVisualTransformation(),
                )
                OutlinedTextField(
                    value = ayarlar.ucNokta,
                    onValueChange = { islemKapsami.launch { depo.ucNoktaAyarla(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("API uç noktası") },
                )
                OutlinedTextField(
                    value = ayarlar.model,
                    onValueChange = { islemKapsami.launch { depo.modelAyarla(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Model") },
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            yonlendirici.apiAnahtariKaydet(ayarlar.saglayici, apiAnahtari)
                            baglantiMesaji = "Anahtar güvenli depoya kaydedildi."
                        },
                        shape = RoundedCornerShape(22.dp),
                    ) { Text("Kaydet") }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            yonlendirici.apiAnahtariSil(ayarlar.saglayici)
                            apiAnahtari = ""
                            baglantiMesaji = "API anahtarı silindi."
                        },
                        shape = RoundedCornerShape(22.dp),
                    ) { Text("Sil") }
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        islemKapsami.launch {
                            baglantiMesaji = when (val durum = yonlendirici.baglantiKontrolu()) {
                                BaglantiDurumu.Bagli -> "Bağlantı başarılı."
                                BaglantiDurumu.BagliDegil -> "Bağlantı yok."
                                is BaglantiDurumu.Hata -> durum.mesaj
                            }
                        }
                    },
                    shape = RoundedCornerShape(22.dp),
                ) { Text("Bağlantıyı test et") }
                if (baglantiMesaji.isNotBlank()) {
                    Text(baglantiMesaji, color = Color(0xFFA9B3C7), fontSize = 12.sp)
                }
            }
        }

        OvalCamPanel {
            AyarSatiri(
                metin = "Harici yapay zekâya veri gönder",
                acik = ayarlar.hariciYapayZekaIzni,
                onDegisim = { islemKapsami.launch { depo.hariciIzniAyarla(it) } },
            )
            AyarSatiri(
                metin = "Sesli yanıt",
                acik = ayarlar.sesliYanıt,
                onDegisim = { islemKapsami.launch { depo.sesliYanitiAyarla(it) } },
            )
            AyarSatiri(
                metin = "Tam giriş animasyonu",
                acik = ayarlar.tamGirisAnimasyonu,
                onDegisim = { islemKapsami.launch { depo.tamGirisAyarla(it) } },
            )
            AyarSatiri(
                metin = "Hareket azaltma",
                acik = ayarlar.hareketAzaltma,
                onDegisim = { islemKapsami.launch { depo.hareketAzaltmaAyarla(it) } },
            )
        }
    }
}

@Composable
private fun AyarSatiri(metin: String, acik: Boolean, onDegisim: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(metin, color = Color(0xFFF7F8FC), modifier = Modifier.weight(1f))
        Switch(checked = acik, onCheckedChange = onDegisim)
    }
}
