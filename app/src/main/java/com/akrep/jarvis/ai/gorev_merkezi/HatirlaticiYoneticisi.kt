package com.akrep.jarvis.ai.gorev_merkezi

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.AlarmClock
import android.provider.Settings

class HatirlaticiYoneticisi(private val context: Context) {
    fun alarmEkraniniAc(mesaj: String, saat: Int, dakika: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, saat)
            putExtra(AlarmClock.EXTRA_MINUTES, dakika)
            putExtra(AlarmClock.EXTRA_MESSAGE, mesaj)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun kesinAlarmIzniAc() {
        if (Build.VERSION.SDK_INT >= 31) {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }
}
