package com.akrep.jarvis.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akrep.jarvis.ai.arayuz.ana_hud.AnaHudEkrani
import com.akrep.jarvis.ai.ayarlar.AyarlarEkrani
import com.akrep.jarvis.ai.canli_gorus.canli_kamera.KameraEkrani
import com.akrep.jarvis.ai.arayuz.giris_ekrani.GirisEkrani
import com.akrep.jarvis.ai.gorev_merkezi.GorevMerkeziEkrani
import com.akrep.jarvis.ai.araclar.tema.AkrepJarvisTema
import com.akrep.jarvis.ai.ayarlar.GizlilikEkrani
import com.akrep.jarvis.ai.gezinme.AkrepNavigasyon

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AkrepJarvisTema {
                var girisTamamlandi by rememberSaveable { mutableStateOf(false) }
                val navController = rememberNavController()

                if (!girisTamamlandi) {
                    GirisEkrani(onGirisTamamlandi = { girisTamamlandi = true })
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = AkrepNavigasyon.HUD,
                    ) {
                        composable(AkrepNavigasyon.HUD) {
                            AnaHudEkrani(
                                onKameraAc = { navController.navigate(AkrepNavigasyon.KAMERA) },
                                onGorevlerAc = { navController.navigate(AkrepNavigasyon.GOREVLER) },
                                onAyarlarAc = { navController.navigate(AkrepNavigasyon.AYARLAR) },
                                onGizlilikAc = { navController.navigate(AkrepNavigasyon.GIZLILIK) },
                            )
                        }
                        composable(AkrepNavigasyon.KAMERA) {
                            KameraEkrani(onKapat = { navController.popBackStack() })
                        }
                        composable(AkrepNavigasyon.GOREVLER) {
                            GorevMerkeziEkrani(onGeri = { navController.popBackStack() })
                        }
                        composable(AkrepNavigasyon.AYARLAR) {
                            AyarlarEkrani(onGeri = { navController.popBackStack() })
                        }
                        composable(AkrepNavigasyon.GIZLILIK) {
                            GizlilikEkrani(onGeri = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
