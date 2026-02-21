package br.com.achimid.animesachimidv2.domains

data class Release(
    val id: String? = null,
    val title: String? = null,
    val animeId: String? = null,
    val animeSlug: String? = null,
    val animeName: String? = null,
    val animeType: String? = null,
    val animeEpisode: String? = null,
    val animeImageUrl: String? = null,
    val animeStreamUrl: String? = null,
    val options: MutableList<EpisodeLinkOptions>? = mutableListOf()
)

