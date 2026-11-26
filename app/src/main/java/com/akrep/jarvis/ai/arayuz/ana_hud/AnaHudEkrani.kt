package com.akrep.jarvis.ai.arayuz.ana_hud

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akrep.jarvis.ai.araclar.bilesenler.AkrepButon
import com.akrep.jarvis.ai.araclar.bilesenler.KristalPanel
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.araclar.tema.BilgiMavi
import com.akrep.jarvis.ai.araclar.tema.EnerjiYesil
import com.akrep.jarvis.ai.araclar.tema.GunesSari
import com.akrep.jarvis.ai.araclar.tema.UzaySiyahi
import com.akrep.jarvis.ai.ayarlar.Ayarlar
import com.akrep.jarvis.ai.ayarlar.AyarlarDeposu
import com.akrep.jarvis.ai.arayuz.gunes_hud.GunesHud
import com.akrep.jarvis.ai.arayuz.gunes_hud.HudDurumu
import com.akrep.jarvis.ai.sesli_asistan.ses_motoru.KonusmaTaniyici
import com.akrep.jarvis.ai.sesli_asistan.ses_motoru.TurkceMetinSeslendirici
import com.akrep.jarvis.ai.sesli_asistan.ses_motoru.UyandiriciKelimeMotoru
import com.akrep.jarvis.ai.araclar.yardimci.PermisyonYardimcisi
import com.akrep.jarvis.ai.sistem_izleyici.SistemDurumuKartlari
import com.akrep.jarvis.ai.ozellikler.BaglamOzellikMenusu
import com.akrep.jarvis.ai.ozellikler.OzellikAlani
import com.akrep.jarvis.ai.ozellikler.OzellikCalistirici
import com.akrep.jarvis.ai.ozellikler.OzellikDeposu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnaHudEkrani(
    viewModel: AnaHudViewModel = viewModel(),
    onKameraAc: () -> Unit,
    onGorevlerAc: () -> Unit,
    onAyarlarAc: () -> Unit,
    onGizlilikAc: () -> Unit,
) {
    val context = LocalContext.current
    val ayarlarDeposu = remember { AyarlarDeposu(context) }
    val ayarlar by ayarlarDeposu.ayarlar.collectAsState(initial = Ayarlar())
    var baglamMenuAcik by remember { mutableStateOf(false) }
    val baglamSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val ozellikCalistirici = remember { OzellikCalistirici(context) }
    var mikrofonIzni by remember {
        androidx.compose.runtime.mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }

    val izinLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { verildi ->
        mikrofonIzni = verildi
        if (verildi) viewModel.durumAyarla(HudDurumu.DINLIYOR)
        else viewModel.hataAyarla("Mikrofon izni verilmedi. Ayarlardan veya sistem izninden etkinleştirin.")
    }
    var sesGenligi by remember { androidx.compose.runtime.mutableStateOf(0f) }

    val taniyici = remember {
        KonusmaTaniyici(
            context,
            onSonuc = { taninan ->
                val temiz = UyandiriciKelimeMotoru.temizle(taninan)
                if (temiz.isBlank()) viewModel.hataAyarla("Bir komut algılanmadı.")
                else viewModel.sesSonucu(temiz)
            },
            onHata = viewModel::hataAyarla,
            onGenlik = { sesGenligi = it },
        )
    }
    val tts = remember { TurkceMetinSeslendirici(context) }
    tts.yayinSonaErdi = { viewModel.durumAyarla(HudDurumu.HAZIR) }

    LaunchedEffect(viewModel.cevap, ayarlar.sesliYanıt) {
        if (viewModel.cevap.isNotBlank() && ayarlar.sesliYanıt) {
            viewModel.durumAyarla(HudDurumu.KONUSUYOR)
            tts.konuş(viewModel.cevap)
        } else if (!ayarlar.sesliYanıt) {
            tts.durdur()
        }
    }

    LaunchedEffect(viewModel.eylem) {
        when (viewModel.eylem) {
            "kamera" -> {
                viewModel.eylemiTuket()
                viewModel.kameraDurumAyarla(true)
                onKameraAc()
            }
            "ayarlar" -> {
                viewModel.eylemiTuket()
                onAyarlarAc()
            }
            "gorevler" -> {
                viewModel.eylemiTuket()
                onGorevlerAc()
            }
            "gizlilik" -> {
                viewModel.eylemiTuket()
                onGizlilikAc()
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
            .statusBarsPadding()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF101B33), Color(0xFF090D1A), UzaySiyahi),
                ),
            ),
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val gridAralik = size.width / 10f
            val gridRenk = BilgiMavi.copy(alpha = 0.045f)
            for (i in 0..10) {
                val x = i * gridAralik
                drawLine(gridRenk, androidx.compose.ui.geometry.Offset(x, 0f), androidx.compose.ui.geometry.Offset(x, size.height), 1.dp.toPx())
            }
            for (i in 0..18) {
                val y = i * size.height / 18f
                drawLine(gridRenk, androidx.compose.ui.geometry.Offset(0f, y), androidx.compose.ui.geometry.Offset(size.width, y), 1.dp.toPx())
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AKREP JARVIS",
                        color = Altin,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "SOLAR KOMUTA MERKEZİ",
                        color = Color(0xFFA9B3C7),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    )
                }
                KristalPanel(modifier = Modifier.fillMaxWidth(0.42f), vurgu = EnerjiYesil) {
                    Text(
                        text = viewModel.hudDurumu.name,
                        color = EnerjiYesil,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                AkrepButon(
                    onClick = { baglamMenuAcik = true },
                    modifier = Modifier.width(48.dp),
                    vurgu = GunesSari,
                ) { Text("•••", fontWeight = FontWeight.Black) }
            }

            KristalPanel(vurgu = Altin) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    GunesHud(
                        durum = viewModel.hudDurumu,
                        hareketAzaltma = ayarlar.hareketAzaltma,
                        genlik = sesGenligi,
                        modifier = Modifier.fillMaxWidth(0.86f),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedVisibility(
                        visible = viewModel.cevap.isNotBlank() || viewModel.hataMesaji != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically(),
                    ) {
                        Text(
                            text = viewModel.hataMesaji ?: viewModel.cevap,
                            color = if (viewModel.hataMesaji != null) Color(0xFFFF5E6C) else Color(0xFFF7F8FC),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AkrepButon(
                    onClick = {
                        if (!mikrofonIzni) izinLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        else if (taniyici.destekleniyor()) {
                            viewModel.durumAyarla(HudDurumu.DINLIYOR)
                            taniyici.dinlemeyeBasla()
                        } else viewModel.hataAyarla("Bu cihazda konuşmadan metne dönüşüm desteklenmiyor.")
                    },
                    modifier = Modifier.weight(1f),
                    vurgu = EnerjiYesil,
                ) { Text("Dinle") }
                AkrepButon(
                    onClick = {
                        viewModel.kameraDurumAyarla(true)
                        onKameraAc()
                    },
                    modifier = Modifier.weight(1f),
                    vurgu = GunesSari,
                ) { Text("Canlı Görüş") }
            }

            KristalPanel {
                OutlinedTextField(
                    value = viewModel.komutMetni,
                    onValueChange = viewModel::komutMetniAyarla,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    label = { Text("Komut veya mesaj") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Altin,
                        unfocusedBorderColor = Color(0xFFA9B3C7),
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))
                AkrepButon(
                    onClick = { viewModel.komutGonder() },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Gönder") }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AkrepButon(onClick = onGorevlerAc, modifier = Modifier.weight(1f), vurgu = BilgiMavi) { Text("Görevler") }
                AkrepButon(onClick = onAyarlarAc, modifier = Modifier.weight(1f), vurgu = GunesSari) { Text("Ayarlar") }
            }

            AkrepButon(
                onClick = onGizlilikAc,
                modifier = Modifier.fillMaxWidth(),
                vurgu = Color(0xFFA9B3C7),
            ) { Text("Gizlilik") }

            SistemDurumuKartlari(viewModel.sistemDurumu)

            if (!mikrofonIzni) {
                KristalPanel(vurgu = Color(0xFFFF5E6C)) {
                    Text(
                        text = "Mikrofon izni verilmedi",
                        color = Color(0xFFFF5E6C),
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AkrepButon(
                        onClick = { PermisyonYardimcisi.uygulamaAyarlariAc(context) },
                        modifier = Modifier.fillMaxWidth(),
                        vurgu = Color(0xFFFFB547),
                    ) { Text("İzin Ayarlarına Git") }
                }
            }
        }

        BaglamOzellikMenusu(
            sheetState = baglamSheetState,
            baslik = "Solar Komuta Özellikleri",
            ozellikler = OzellikDeposu.alanicOzellikler(
                OzellikAlani.HUD,
                OzellikAlani.SISTEM,
                OzellikAlani.OTOMASYON,
            ).take(14),
            acik = baglamMenuAcik,
            onKapat = { baglamMenuAcik = false },
            onSec = { ozellik ->
                when (ozellik.anahtar) {
                    "canli_gorus" -> onKameraAc()
                    "gorev_merkezi" -> onGorevlerAc()
                    "sesli_asistan" -> {
                        if (mikrofonIzni) taniyici.dinlemeyeBasla()
                        else izinLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                    "ayarlar" -> onAyarlarAc()
                    "gizlilik", "konum_gecmisi", "acil_durum" -> ozellikCalistirici.calistir(ozellik.anahtar)
                    else -> ozellikCalistirici.calistir(ozellik.anahtar)
                }
            },
        )
    }
}
