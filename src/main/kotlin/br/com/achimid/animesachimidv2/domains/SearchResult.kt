package br.com.achimid.animesachimidv2.domains

data class SearchResult(
    val anime: Anime,
    val score: Int,
    val needsReview: Boolean,
    val rawTitle: String,
)
