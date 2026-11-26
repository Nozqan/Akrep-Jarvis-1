package com.akrep.jarvis.ai.ozellikler

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.Settings

class OzellikCalistirici(private val context: Context) {
    fun calistir(anahtar: String): Boolean =
        runCatching {
            val intent = when (anahtar) {
                "dosya_yonetimi", "dosya_paylasimi" ->
                    Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "takvim", "akilli_takvim_randevu" ->
                    Intent(Intent.ACTION_INSERT).setData(android.provider.CalendarContract.Events.CONTENT_URI)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "hava_durumu" ->
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://weather.com")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "konum_harita", "konum_gecmisi" ->
                    Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=bulunduğum konum"))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "ceviri", "mesaj_taslaklari", "klavye_asistani" ->
                    Intent(Intent.ACTION_SEND).setType("text/plain").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "alarm_merkezi" ->
                    Intent(AlarmClock.ACTION_SHOW_ALARMS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "zamanlayici" ->
                    Intent(AlarmClock.ACTION_SHOW_TIMERS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "bildirim_merkezi", "rahatsiz_etme" ->
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "wifi_yonetimi" ->
                    Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "bluetooth_yonetimi" ->
                    Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "mobil_veri_kontrol", "veri_tasarrufu" ->
                    Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "aydinlik_ekran" ->
                    Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "ses_profilleri", "medya_kontrol" ->
                    Intent(Settings.ACTION_SOUND_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "ekran_okuma" ->
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "sifre_kasasi", "hata_raporu" ->
                    Intent(Settings.ACTION_SECURITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "veri_yedekleme", "yedekleme_gecmisi" ->
                    Intent(Settings.ACTION_PRIVACY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "pil_optimizasyonu" ->
                    Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "cihaz_diagnostik", "uygulama_kullanim" ->
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "ag_trafik", "ag_dosya_tarama" ->
                    Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "telefon_kontak", "arama_yardimcisi" ->
                    Intent(Intent.ACTION_DIAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                "acil_durum" ->
                    Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:*"))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                else -> Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        }.getOrDefault(false)
}
