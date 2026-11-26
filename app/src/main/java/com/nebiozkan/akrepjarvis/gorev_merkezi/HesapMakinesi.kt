package com.nebiozkan.akrepjarvis.gorev_merkezi

object HesapMakinesi {
    fun hesapla(ifade: String): Double? {
        val normalized = ifade
            .replace(",", ".")
            .replace(Regex("[^0-9+\\-*/(). ]"), "")
            .replace(Regex("\\s+"), "")
            .trim()
        if (normalized.isBlank()) return null
        return runCatching {
            Parser(normalized).parse()
        }.getOrNull()
    }

    private class Parser(private val ifade: String) {
        private var index = 0

        fun parse(): Double? {
            val sonuc = toplamaCikarma()
            return if (index == ifade.length) sonuc else null
        }

        private fun toplamaCikarma(): Double? {
            var sol = carpmaBolme() ?: return null
            while (index < ifade.length) {
                if (ifade[index] == '+') {
                    index++
                    sol += carpmaBolme() ?: return null
                } else if (ifade[index] == '-') {
                    index++
                    sol -= carpmaBolme() ?: return null
                } else break
            }
            return sol
        }

        private fun carpmaBolme(): Double? {
            var sol = parantez() ?: return null
            while (index < ifade.length) {
                if (ifade[index] == '*') {
                    index++
                    sol *= parantez() ?: return null
                } else if (ifade[index] == '/') {
                    index++
                    val sag = parantez() ?: return null
                    if (sag == 0.0) return null
                    sol /= sag
                } else break
            }
            return sol
        }

        private fun parantez(): Double? {
            if (index >= ifade.length) return null
            if (ifade[index] == '(') {
                index++
                val sonuc = toplamaCikarma()
                if (index >= ifade.length || ifade[index] != ')') return null
                index++
                return sonuc
            }
            if (ifade[index] == '+' || ifade[index] == '-') {
                val isaret = if (ifade[index] == '-') -1.0 else 1.0
                index++
                return isaret * (parantez() ?: return null)
            }
            val baslangic = index
            while (index < ifade.length &&
                (ifade[index].isDigit() || ifade[index] == '.')
            ) {
                index++
            }
            if (baslangic == index) return null
            return ifade.substring(baslangic, index).toDoubleOrNull()
        }
    }
}
