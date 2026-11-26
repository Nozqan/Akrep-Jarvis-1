package com.akrep.jarvis.ai.ozellikler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akrep.jarvis.ai.araclar.bilesenler.AkrepButon
import com.akrep.jarvis.ai.araclar.tema.GunesSari
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaglamOzellikMenusu(
    sheetState: SheetState,
    baslik: String,
    ozellikler: List<OzellikKaydi>,
    acik: Boolean,
    onKapat: () -> Unit,
    onSec: (OzellikKaydi) -> Unit,
) {
    if (!acik) return
    val kapsam = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onKapat,
        sheetState = sheetState,
        containerColor = Color(0xFF101B33),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = baslik.uppercase(),
                color = GunesSari,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            ozellikler.forEach { ozellik ->
                AkrepButon(
                    onClick = {
                        onSec(ozellik)
                        kapsam.launch { sheetState.hide() }
                        onKapat()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    vurgu = GunesSari,
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ozellik.ad, fontWeight = FontWeight.Bold)
                            Text(ozellik.aciklama, fontSize = 11.sp, color = Color(0xFFA9B3C7))
                        }
                    }
                }
            }
        }
    }
}
