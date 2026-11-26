package com.akrep.jarvis.ai.gorev_merkezi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.AlarmClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.akrep.jarvis.ai.araclar.bilesenler.AkrepButon
import com.akrep.jarvis.ai.araclar.bilesenler.KristalPanel
import com.akrep.jarvis.ai.araclar.tema.Altin
import com.akrep.jarvis.ai.araclar.tema.BilgiMavi
import com.akrep.jarvis.ai.araclar.tema.EnerjiYesil
import com.akrep.jarvis.ai.araclar.tema.GunesSari
import com.akrep.jarvis.ai.araclar.tema.UzaySiyahi
import com.akrep.jarvis.ai.veri.depo.NotYoneticisi
import com.akrep.jarvis.ai.veri.depo.SohbetGecmisiYoneticisi
import com.akrep.jarvis.ai.veri.model.NotKaydi

@Composable
fun GorevMerkeziEkrani(onGeri: () -> Unit) {
    val context = LocalContext.current
    val notYoneticisi = remember { NotYoneticisi(context) }
    val notlar = remember {
        mutableStateListOf<NotKaydi>().apply { addAll(notYoneticisi.hepsi()) }
    }
    val yeniNot = remember { mutableStateOf("") }
    val arama = remember { mutableStateOf("") }
    val durumMesaji = remember { mutableStateOf("") }
    val hatirlatmaMesaji = remember { mutableStateOf("") }
    val saat = remember { mutableStateOf("09") }
    val dakika = remember { mutableStateOf("30") }
    val hatirlatici = remember { HatirlaticiYoneticisi(context) }
    val sohbetGecmisi = remember { SohbetGecmisiYoneticisi(context).hepsi() }

    val bildirimLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { verildi ->
        durumMesaji.value = if (verildi) "Bildirim izni verildi." else "Bildirim izni verilmedi."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Brush.verticalGradient(listOf(Color(0xFF101B33), UzaySiyahi))),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    AkrepButon(
                        onClick = onGeri,
                        modifier = Modifier.fillMaxWidth(0.28f),
                        vurgu = Color(0xFFA9B3C7),
                    ) { Text("Geri") }
                    KristalPanel(modifier = Modifier.fillMaxWidth(0.68f), vurgu = Altin) {
                        Text(
                            text = "GÖREV MERKEZİ",
                            color = Altin,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        )
                    }
                }
            }

            item {
                KristalPanel(vurgu = GunesSari) {
                    Text("HIZLI NOT", color = GunesSari, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = yeniNot.value,
                        onValueChange = { yeniNot.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        label = { Text("Yeni not") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GunesSari),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AkrepButon(
                        onClick = {
                            if (yeniNot.value.isBlank()) durumMesaji.value = "Not içeriği boş olamaz."
                            else {
                                val kayit = notYoneticisi.ekle(yeniNot.value)
                                notlar.add(0, kayit)
                                yeniNot.value = ""
                                durumMesaji.value = "Not oluşturuldu."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        vurgu = GunesSari,
                    ) { Text("Not Oluştur") }
                }
            }

            item {
                KristalPanel(vurgu = BilgiMavi) {
                    OutlinedTextField(
                        value = arama.value,
                        onValueChange = { sorgu ->
                            arama.value = sorgu
                            notlar.clear()
                            notlar.addAll(notYoneticisi.ara(sorgu))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        label = { Text("Notlarda ara") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BilgiMavi),
                    )
                }
            }

            item {
                KristalPanel(vurgu = EnerjiYesil) {
                    Text("HATIRLATICI", color = EnerjiYesil, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = hatirlatmaMesaji.value,
                        onValueChange = { hatirlatmaMesaji.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        label = { Text("Hatırlatıcı mesajı") },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = saat.value,
                            onValueChange = { saat.value = it.filter(Char::isDigit).take(2) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            label = { Text("Saat") },
                        )
                        OutlinedTextField(
                            value = dakika.value,
                            onValueChange = { dakika.value = it.filter(Char::isDigit).take(2) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            label = { Text("Dakika") },
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    AkrepButon(
                        onClick = {
                            val saatDegeri = saat.value.toIntOrNull()?.coerceIn(0, 23)
                            val dakikaDegeri = dakika.value.toIntOrNull()?.coerceIn(0, 59)
                            if (saatDegeri == null || dakikaDegeri == null) {
                                durumMesaji.value = "Saat ve dakika doğru aralıkta olmalı."
                            } else {
                                hatirlatici.alarmEkraniniAc(
                                    hatirlatmaMesaji.value.ifBlank { "AKREP JARVIS hatırlatıcısı" },
                                    saatDegeri,
                                    dakikaDegeri,
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        vurgu = EnerjiYesil,
                    ) { Text("Alarm Uygulamasını Aç") }
                    Spacer(modifier = Modifier.height(8.dp))
                    AkrepButon(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= 33 &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                bildirimLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                durumMesaji.value = "Bildirim izni kullanılabilir durumda."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        vurgu = BilgiMavi,
                    ) { Text("Bildirim İzni") }
                }
            }

            if (durumMesaji.value.isNotBlank()) {
                item {
                    KristalPanel(vurgu = GunesSari) {
                        Text(durumMesaji.value, color = Color(0xFFF7F8FC), fontSize = 12.sp)
                    }
                }
            }

            if (sohbetGecmisi.isNotEmpty()) {
                item {
                    KristalPanel(vurgu = BilgiMavi) {
                        Text("SON SOHBETLER", color = BilgiMavi, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        sohbetGecmisi.take(3).forEach { kayit ->
                            Text(
                                text = "Sen: ${kayit.kullaniciMesaji}\nJarvis: ${kayit.yanit}",
                                color = Color(0xFFA9B3C7),
                                fontSize = 12.sp,
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
            }

            items(notlar, key = { it.id }) { not ->
                KristalPanel(vurgu = GunesSari) {
                    Text(not.metin, color = Color(0xFFF7F8FC))
                    Spacer(modifier = Modifier.height(8.dp))
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
