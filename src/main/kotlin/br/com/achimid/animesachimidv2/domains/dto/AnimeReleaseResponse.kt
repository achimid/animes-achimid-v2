package br.com.achimid.animesachimidv2.domains.dto

import java.util.*

data class AnimeReleaseResponse(
    val id: UUID? = null,
    val animeSlug: String = "sousou-no-frieren", // TODO: Corrigir esse default
    val animeTitle: String,
    val animeType: String? = "Episódio",
    val animeNumber: String? = null,
    val animeImageUrl: String? = null,
    val options: List<EpisodeLinkOptionsDTO>? = emptyList()
)

data class AnimeReleasesResponse(
    val releases: List<AnimeReleaseResponse> = emptyList()
)
