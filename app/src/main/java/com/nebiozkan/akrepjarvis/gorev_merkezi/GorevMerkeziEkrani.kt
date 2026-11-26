package com.nebiozkan.akrepjarvis.gorev_merkezi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.AlarmClock
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nebiozkan.akrepjarvis.ortak.bilesenler.OvalCamPanel
import com.nebiozkan.akrepjarvis.ortak.tema.Altin
import com.nebiozkan.akrepjarvis.ortak.tema.UzaySiyahi
import com.nebiozkan.akrepjarvis.veri.depo.NotYoneticisi
import com.nebiozkan.akrepjarvis.veri.model.NotKaydi

@Composable
fun GorevMerkeziEkrani(onGeri: () -> Unit) {
    val context = LocalContext.current
    val notYoneticisi = remember { NotYoneticisi(context) }
    val notlar = remember { mutableStateListOf<NotKaydi>().apply { addAll(notYoneticisi.hepsi()) } }
    val yeniNot = remember { mutableStateOf("") }
    val arama = remember { mutableStateOf("") }
    val bildirimDurumu = remember { mutableStateOf("") }
    val hatirlaticiMesaji = remember { mutableStateOf("") }
    val saat = remember { mutableStateOf("09") }
    val dakika = remember { mutableStateOf("30") }
    val hatirlaticiYoneticisi = remember { HatirlaticiYoneticisi(context) }

    val bildirimLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { verildi ->
        bildirimDurumu.value =
            if (verildi) "Bildirim izni verildi." else "Bildirim izni verilmedi; ayarlardan etkinleştirilebilir."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = onGeri, shape = RoundedCornerShape(22.dp)) { Text("Geri") }
            Text("Görev Merkezi", color = Altin, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        OvalCamPanel {
            OutlinedTextField(
                value = yeniNot.value,
                onValueChange = { yeniNot.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Yeni not") },
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (yeniNot.value.isNotBlank()) {
                        val kayit = notYoneticisi.ekle(yeniNot.value)
                        notlar.add(0, kayit)
                        yeniNot.value = ""
                    }
                },
                shape = RoundedCornerShape(22.dp),
            ) { Text("Metinle not oluştur") }
        }

        OvalCamPanel {
            OutlinedTextField(
                value = arama.value,
                onValueChange = { sorgu ->
                    arama.value = sorgu
                    notlar.clear()
                    notlar.addAll(notYoneticisi.ara(sorgu))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notlarda ara") },
            )
        }

        OvalCamPanel {
            OutlinedTextField(
                value = hatirlaticiMesaji.value,
                onValueChange = { hatirlaticiMesaji.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Hatırlatıcı mesajı") },
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = saat.value,
                    onValueChange = { saat.value = it.filter(Char::isDigit).take(2) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Saat") },
                )
                OutlinedTextField(
                    value = dakika.value,
                    onValueChange = { dakika.value = it.filter(Char::isDigit).take(2) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Dakika") },
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
                    val saatDegeri = saat.value.toIntOrNull()?.coerceIn(0, 23)
                    val dakikaDegeri = dakika.value.toIntOrNull()?.coerceIn(0, 59)
                    if (saatDegeri == null || dakikaDegeri == null) {
                        bildirimDurumu.value = "Saat 0-23, dakika 0-59 arasında olmalı."
                    } else {
                        hatirlaticiYoneticisi.alarmEkraniniAc(
                            hatirlaticiMesaji.value.ifBlank { "AKREP JARVIS hatırlatıcısı" },
                            saatDegeri,
                            dakikaDegeri,
                        )
                    }
                },
                shape = RoundedCornerShape(22.dp),
            ) { Text("Hatırlatıcı oluştur") }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (Build.VERSION.SDK_INT >= 33 &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                        PackageManager.PERMISSION_GRANTED
                    ) {
                        bildirimLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        bildirimDurumu.value = "Bildirim izni kullanılabilir durumda."
                    }
                },
                shape = RoundedCornerShape(22.dp),
            ) { Text("Bildirim izni") }

            if (Build.VERSION.SDK_INT >= 31) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { hatirlaticiYoneticisi.kesinAlarmIzniAc() },
                    shape = RoundedCornerShape(22.dp),
                ) { Text("Kesin alarm") }
            }
        }

        if (bildirimDurumu.value.isNotBlank()) {
            Text(bildirimDurumu.value, color = Color(0xFFA9B3C7), fontSize = 12.sp)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(notlar, key = { it.id }) { not ->
                OvalCamPanel {
                    Text(not.metin, color = Color(0xFFF7F8FC))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = {
                            notYoneticisi.sil(not.id)
                            notlar.remove(not)
                        },
                        shape = RoundedCornerShape(22.dp),
                    ) { Text("Sil") }
                }
            }
        }
    }
}
