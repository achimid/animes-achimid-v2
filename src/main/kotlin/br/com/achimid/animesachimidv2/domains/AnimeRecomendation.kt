package br.com.achimid.animesachimidv2.domains

import kotlin.random.Random

data class AnimeRecommendation(
    val recommendations: List<Recommendation>? = emptyList(),
)

data class Recommendation(
    val id: String,
    val slug: String,
    val title: String,
    val imageUrl: String,
    val meta: String,
    val rating: Double? = Random.nextDouble(from = 0.0, until = 10.0),
)



