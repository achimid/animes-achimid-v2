package br.com.achimid.animesachimidv2.utils

fun normalizeTitle(title: String): String = title
    .replace(Regex("""\b(\d{1,2})(st|nd|rd|th)\s+season\b""", RegexOption.IGNORE_CASE), "Season $1")
    .replace(Regex("""\bS(\d{1,2})\b"""), "Season $1")
    .replace(Regex("""\bCour\s+(\d{1,2})\b""", RegexOption.IGNORE_CASE), "Season $1")
    .replace(Regex("""\(\d{4}\)"""), "")
    .replace(Regex("""[:!?]"""), " ")
    .replace(Regex("""\s{2,}"""), " ")
    .trim()

fun stripSeasonFromTitle(title: String): String = title
    .replace(Regex("""\b(season\s+\d{1,2}|\d{1,2}(st|nd|rd|th)\s+season|part\s+\d{1,2}|cour\s+\d{1,2}|s\d{1,2})\b""", RegexOption.IGNORE_CASE), "")
    .replace(Regex("""\(\d{4}\)"""), "")
    .replace(Regex("""\s{2,}"""), " ")
    .trim()

fun String?.padLeft(length: Int = 3): String? {
    val episodeNumber = this?.toIntOrNull() ?: return this

    return "$episodeNumber".padStart(length, '0')
}

fun String?.unpadLeft(): String? {
    return this?.trimStart('0')?.ifEmpty { "0" }
}