package br.com.achimid.animesachimidv2.domains.dto

import kotlin.math.round
import kotlin.random.Random.Default.nextDouble

data class AnimeRecommendationDTO(
    val recommendationDTOS: List<RecommendationDTO>? = emptyList(),
)

data class RecommendationDTO(
    val id: String,
    val slug: String,
    val title: String,
    val imageUrl: String,
    val meta: String,
    val rating: Double? = round(nextDouble(from = 0.0, until = 10.0) * 100) / 100,
)



