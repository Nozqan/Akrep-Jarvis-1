package com.nebiozkan.akrepjarvis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.nebiozkan.akrepjarvis.ana_hud.AnaHudEkrani
import com.nebiozkan.akrepjarvis.giris_ekrani.GirisEkrani
import com.nebiozkan.akrepjarvis.ortak.tema.AkrepJarvisTema

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AkrepJarvisTema {
                var girisTamamlandi by rememberSaveable { mutableStateOf(false) }

                if (girisTamamlandi) {
                    AnaHudEkrani()
                } else {
                    GirisEkrani(
                        onGirisTamamlandi = { girisTamamlandi = true },
                    )
                }
            }
        }
    }
}
