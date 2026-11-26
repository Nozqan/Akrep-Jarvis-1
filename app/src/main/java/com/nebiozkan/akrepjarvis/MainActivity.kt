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
                        startDestination = "hud",
                    ) {
                        composable("hud") {
                            AnaHudEkrani(
                                onKameraAc = { navController.navigate("kamera") },
                                onGorevlerAc = { navController.navigate("gorevler") },
                                onAyarlarAc = { navController.navigate("ayarlar") },
                                onGizlilikAc = { navController.navigate("gizlilik") },
                            )
                        }
                        composable("kamera") {
                            KameraEkrani(onKapat = { navController.popBackStack() })
                        }
                        composable("gorevler") {
                            GorevMerkeziEkrani(onGeri = { navController.popBackStack() })
                        }
                        composable("ayarlar") {
                            AyarlarEkrani(onGeri = { navController.popBackStack() })
                        }
                        composable("gizlilik") {
                            GizlilikEkrani(onGeri = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
