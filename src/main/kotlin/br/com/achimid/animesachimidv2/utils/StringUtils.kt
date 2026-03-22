package br.com.achimid.animesachimidv2.utils

fun String?.padLeft(length: Int = 3): String? {
    val episodeNumber = this?.toIntOrNull() ?: return this

    return "$episodeNumber".padStart(length, '0')
}

fun String?.unpadLeft(): String? {
    return this?.trimStart('0')?.ifEmpty { "0" }
}