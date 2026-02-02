package br.com.achimid.animesachimidv2.domains

data class AnimeRecommendation(
    val recommendations: List<Recommendation>? = emptyList(),
)

data class Recommendation(
    val id: String,
    val title: String,
    val imageUrl: String,
    val meta: String
)



