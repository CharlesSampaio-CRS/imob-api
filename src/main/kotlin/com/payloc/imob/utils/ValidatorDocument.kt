package com.payloc.imob.utils

class ValidatorDocument(private val cpf: String) {

    fun isValid(): Boolean {
        val cleanedCpf = cpf.replace(Regex("[^0-9]"), "")
        if (cleanedCpf.length != 11) return false
        if (cleanedCpf.all { it == cleanedCpf[0] }) return false

        val firstVerifier = calculateVerifier(cleanedCpf.substring(0, 9), 10)
        val secondVerifier = calculateVerifier(cleanedCpf.substring(0, 9) + firstVerifier, 11)

        return cleanedCpf[9] == firstVerifier && cleanedCpf[10] == secondVerifier
    }

    private fun calculateVerifier(cpfSubstring: String, multiplier: Int): Char {
        var sum = 0
        for (i in cpfSubstring.indices) {
            sum += cpfSubstring[i].digitToInt() * (multiplier - i)
        }
        val remainder = sum % 11
        return if (remainder < 2) {
            '0'
        } else {
            (11 - remainder).toString()[0]
        }
    }
}
