package br.com.achimid.animesachimidv2.domains

import java.util.*

data class AnimeRelease(
    val id: UUID? = null,
    val animeTitle: String,
    val animeType: String? = null,
    val animeNumber: String? = null,
)
