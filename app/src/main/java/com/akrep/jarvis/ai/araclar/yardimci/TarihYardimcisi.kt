package com.akrep.jarvis.ai.araclar.yardimci

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object TarihYardimcisi {
    fun zamanMetni(zaman: Long): String =
        DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm", Locale("tr", "TR"))
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(zaman))
}
