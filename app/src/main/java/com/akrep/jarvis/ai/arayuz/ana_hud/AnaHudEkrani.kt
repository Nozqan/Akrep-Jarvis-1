package com.akrep.jarvis.ai.arayuz.ana_hud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.araclar.tema.CamYuzey
import com.akrep.jarvis.ai.araclar.tema.UzaySiyahi
import com.akrep.jarvis.ai.sistem_izleyici.SistemDurumuKartlari
import com.akrep.jarvis.ai.arayuz.gunes_hud.GunesHud
import com.akrep.jarvis.ai.araclar.bilesenler.OvalCamPanel
import com.akrep.jarvis.ai.sesli_asistan.ses_motoru.KonusmaTaniyici
import com.akrep.jarvis.ai.sesli_asistan.ses_motoru.TurkceMetinSeslendirici
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.akrep.jarvis.ai.ayarlar.Ayarlar
import com.akrep.jarvis.ai.ayarlar.AyarlarDeposu
import com.akrep.jarvis.ai.sesli_asistan.ses_motoru.UyandiriciKelimeMotoru

@Composable
fun AnaHudEkrani(
    viewModel: AnaHudViewModel = viewModel(),
    onKameraAc: () -> Unit,
    onGorevlerAc: () -> Unit,
    onAyarlarAc: () -> Unit,
    onGizlilikAc: () -> Unit,
) {
    val durum = viewModel.durum
    val context = LocalContext.current
    val ayarlarDeposu = remember { AyarlarDeposu(context) }
    val ayarlar by ayarlarDeposu.ayarlar.collectAsState(initial = Ayarlar())
    var mikrofonIzniVerildi = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO,
    ) == PackageManager.PERMISSION_GRANTED
    val mikrofonLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { verildi ->
        if (verildi) viewModel.durumAyarla(com.akrep.jarvis.ai.arayuz.gunes_hud.HudDurumu.DINLIYOR)
    }

    val taniyici = remember {
        KonusmaTaniyici(
            context,
            onSonuc = { taninan ->
                val temiz = UyandiriciKelimeMotoru.temizle(taninan)
                if (temiz.isBlank()) {
                    viewModel.hataAyarla("Bir komut algılanmadı.")
                } else {
                    viewModel.sesSonucu(temiz)
                }
            },
            onHata = { viewModel.hataAyarla(it) },
        )
    }
    val tts = remember { TurkceMetinSeslendirici(context) }
    tts.yayinSonaErdi = {
        viewModel.durumAyarla(com.akrep.jarvis.ai.arayuz.gunes_hud.HudDurumu.HAZIR)
    }

    LaunchedEffect(viewModel.cevap, ayarlar.sesliYanıt) {
        if (viewModel.cevap.isNotBlank() && ayarlar.sesliYanıt) {
            viewModel.durumAyarla(com.akrep.jarvis.ai.arayuz.gunes_hud.HudDurumu.KONUSUYOR)
            tts.konuş(viewModel.cevap)
        }
    }

    LaunchedEffect(viewModel.eylem) {
        when (viewModel.eylem) {
            "kamera" -> {
                viewModel.eylemiTuket()
                onKameraAc()
            }
            "ayarlar" -> {
                viewModel.eylemiTuket()
                onAyarlarAc()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            taniyici.serbestBirak()
            tts.serbestBirak()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B1224), UzaySiyahi),
                ),
            )
            .padding(horizontal = 18.dp, vertical = 42.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "AKREP JARVIS",
                color = Altin,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            OvalCamPanel(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    GunesHud(
                        viewModel.hudDurumu,
                        hareketAzaltma = ayarlar.hareketAzaltma,
                    )
                    Text(
                        text = if (viewModel.hataMesaji != null) viewModel.hataMesaji!! else viewModel.cevap.ifBlank { durum },
                        color = if (viewModel.hataMesaji != null) Color(0xFFFF5E6C) else Color(0xFFA9B3C7),
                        fontSize = 13.sp,
                        maxLines = 3,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (!mikrofonIzniVerildi) {
                            mikrofonLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else if (taniyici.destekleniyor()) {
                            viewModel.durumAyarla(com.akrep.jarvis.ai.arayuz.gunes_hud.HudDurumu.DINLIYOR)
                            taniyici.dinlemeyeBasla()
                        } else {
                            viewModel.hataAyarla("Bu cihazda konuşmadan metne dönüşüm desteklenmiyor.")
                        }
                    },
                    shape = RoundedCornerShape(22.dp),
                ) { Text("Dinle") }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onKameraAc,
                    shape = RoundedCornerShape(22.dp),
                ) { Text("Kamera") }
            }

            OutlinedTextField(
                value = viewModel.komutMetni,
                onValueChange = viewModel::komutMetniAyarla,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Komut veya mesaj") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Altin,
                    unfocusedBorderColor = Color(0xFFA9B3C7),
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.komutGonder() },
                    shape = RoundedCornerShape(22.dp),
                ) { Text("Gönder") }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onGorevlerAc,
                    shape = RoundedCornerShape(22.dp),
                ) { Text("Görevler") }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onAyarlarAc,
                    shape = RoundedCornerShape(22.dp),
                ) { Text("Ayarlar") }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onGizlilikAc,
                    shape = RoundedCornerShape(22.dp),
                ) { Text("Gizlilik") }
            }

            SistemDurumuKartlari(viewModel.sistemDurumu)
        }
    }
}
