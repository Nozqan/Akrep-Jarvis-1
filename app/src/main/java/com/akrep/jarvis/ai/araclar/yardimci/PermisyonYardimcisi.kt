package com.akrep.jarvis.ai.araclar.yardimci

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object PermisyonYardimcisi {
    fun uygulamaAyarlariAc(context: Context) {
        context.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}"),
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}
