package br.com.achimid.animesachimidv2.domains

import java.util.*

data class Anime(
    val id: UUID,
    val slug: String,
    val title: String,
    val detail: AnimeDetail,
    val malIntegration: AnimeMALIntegration? = null,
)
