package br.com.achimid.animesachimidv2.domains

import java.util.*

data class AnimeRelease(
    val id: UUID? = null,
    val animeTitle: String,
    val animeType: String? = "Episódio",
    val animeNumber: String? = null,
    val animeImageUrl: String? = null,
    val animeSlug: String = "sousou-no-frieren",
    val options: List<EpisodeLinkOptions>? = emptyList()
)
