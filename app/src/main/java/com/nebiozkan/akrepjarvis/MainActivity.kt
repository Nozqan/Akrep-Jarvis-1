package com.nebiozkan.akrepjarvis

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
import com.nebiozkan.akrepjarvis.ana_hud.AnaHudEkrani
import com.nebiozkan.akrepjarvis.ayarlar.AyarlarEkrani
import com.nebiozkan.akrepjarvis.canli_kamera.KameraEkrani
import com.nebiozkan.akrepjarvis.giris_ekrani.GirisEkrani
import com.nebiozkan.akrepjarvis.gorev_merkezi.GorevMerkeziEkrani
import com.nebiozkan.akrepjarvis.ortak.tema.AkrepJarvisTema
import com.nebiozkan.akrepjarvis.ayarlar.GizlilikEkrani
import com.nebiozkan.akrepjarvis.gezinme.AkrepNavigasyon

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
