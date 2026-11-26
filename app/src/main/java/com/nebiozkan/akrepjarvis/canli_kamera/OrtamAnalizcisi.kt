package com.nebiozkan.akrepjarvis.canli_kamera

class OrtamAnalizcisi {
    fun analizEt(nesneEtiketleri: List<String>): String {
        if (nesneEtiketleri.isEmpty()) {
            return "Ortamda güvenilir bir nesne algılanmadı."
        }
        return "Algılanan ortam ögeleri: ${nesneEtiketleri.joinToString(", ")}."
    }
}
