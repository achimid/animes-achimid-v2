package br.com.achimid.animesachimidv2.domains

import java.util.*

data class Release(
    val id: UUID? = null,
    val animeSlug: String = "sousou-no-frieren", // TODO: Corrigir esse default
    val animeTitle: String,
    val animeType: String? = "Episódio",
    val animeNumber: String? = null,
    val animeId: String? = null,
    val animeImageUrl: String? = null,
    val options: List<EpisodeLinkOptions>? = emptyList()
)

