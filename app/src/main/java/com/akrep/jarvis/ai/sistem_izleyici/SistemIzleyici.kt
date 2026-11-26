package com.akrep.jarvis.ai.sistem_izleyici

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs

data class SistemDurumu(
    val batarya: String = "Kullanılamıyor",
    val bataryaSicakligi: String = "Kullanılamıyor",
    val depolama: String = "Kullanılamıyor",
    val bellek: String = "Kullanılamıyor",
    val ag: String = "Kullanılamıyor",
    val agHizi: String = "Kullanılamıyor",
    val mikrofon: String = "Dinleme kapalı",
    val kamera: String = "Kamera kapalı",
    val yapayZeka: String = "Yerel motor hazır",
)

class SistemIzleyici(private val context: Context) {
    fun guncelDurumuAl(): SistemDurumu = SistemDurumu(
        batarya = bataryaDurumu(),
        bataryaSicakligi = bataryaSicakligi(),
        depolama = depolamaDurumu(),
        bellek = bellekDurumu(),
        ag = agDurumu(),
        agHizi = agHizi(),
    )

    private fun bataryaDurumu(): String {
        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
        ) ?: return "Kullanılamıyor"

        val yuzde = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val olcek = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (yuzde < 0 || olcek <= 0) return "Kullanılamıyor"

        val durum = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val sarjOluyor = durum == BatteryManager.BATTERY_STATUS_CHARGING ||
            durum == BatteryManager.BATTERY_STATUS_FULL

        val yuzdeMetni = "${yuzde * 100 / olcek}%"
        return if (sarjOluyor) "$yuzdeMetni · Şarj oluyor" else yuzdeMetni
    }

    private fun bataryaSicakligi(): String {
        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
        ) ?: return "Kullanılamıyor"
        val sicaklik = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        return if (sicaklik >= 0) "${sicaklik / 10.0} °C" else "Kullanılamıyor"
    }

    private fun depolamaDurumu(): String {
        val yol = Environment.getDataDirectory()
        val alan = StatFs(yol.path)
        val toplam = alan.totalBytes
        val bos = alan.availableBytes
        if (toplam <= 0) return "Kullanılamıyor"
        val kullanilan = toplam - bos
        val gb = 1024L * 1024L * 1024L
        return "${kullanilan / gb} GB kullanılan · ${bos / gb} GB boş"
    }

    private fun bellekDurumu(): String {
        val yonetici = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            ?: return "Kullanılamıyor"
        val bilgi = ActivityManager.MemoryInfo()
        yonetici.getMemoryInfo(bilgi)
        val gb = 1024L * 1024L * 1024L
        return "${bilgi.availMem / gb} GB boş / ${bilgi.totalMem / gb} GB"
    }

    private fun agDurumu(): String {
        val yonetici = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return "Kullanılamıyor"
        val ag = yonetici.activeNetwork ?: return "Çevrimdışı"
        val ozellikler = yonetici.getNetworkCapabilities(ag) ?: return "Kullanılamıyor"
        if (!ozellikler.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            return "Çevrimdışı"
        }
        return when {
            ozellikler.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi bağlı"
            ozellikler.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobil veri bağlı"
            ozellikler.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet bağlı"
            ozellikler.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> "İnternet bağlı"
            else -> "Bağlantı türü kullanılamıyor"
        }
    }

    private fun agHizi(): String {
        val yonetici = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return "Kullanılamıyor"
        val ag = yonetici.activeNetwork ?: return "Çevrimdışı"
        val ozellikler = yonetici.getNetworkCapabilities(ag) ?: return "Kullanılamıyor"
        if (!ozellikler.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ||
            !ozellikler.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        ) {
            return "Çevrimdışı"
        }
        val hızı = ozellikler.linkDownstreamBandwidthKbps
        return if (hızı > 0) "~$hızı kb/sn" else "Kullanılamıyor"
    }
}
