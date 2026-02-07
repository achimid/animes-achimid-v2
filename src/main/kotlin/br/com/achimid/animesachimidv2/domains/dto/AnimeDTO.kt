package br.com.achimid.animesachimidv2.domains.dto

data class AnimeDTO(
    val slug: String,
    val title: String,
    val season: Int? = 1,
    val detail: AnimeDetailDTO,
    val episodes: List<EpisodeInfoDTO>? = null,
) {
    val fullTitle: String = "$title - ${season}º Temporada"
}
