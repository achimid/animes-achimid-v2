package br.com.achimid.animesachimidv2.domains

/** Próximo episódio em exibição de um anime (FUNC-13, via AniList). */
data class NextEpisode(
    val episode: Int,
    val airingAt: Long,
    val timeUntilAiring: Long,
) {
    /** Contagem regressiva amigável: "2d 3h", "5h 20min", "em breve". */
    fun countdown(): String {
        if (timeUntilAiring <= 0) return "agora"

        val days = timeUntilAiring / 86400
        val hours = (timeUntilAiring % 86400) / 3600
        val minutes = (timeUntilAiring % 3600) / 60

        return buildString {
            if (days > 0) append("${days}d ")
            if (hours > 0) append("${hours}h ")
            if (days == 0L && minutes > 0) append("${minutes}min")
        }.trim().ifEmpty { "em breve" }
    }
}
