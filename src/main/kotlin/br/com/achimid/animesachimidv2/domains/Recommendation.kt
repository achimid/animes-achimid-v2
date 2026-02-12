package br.com.achimid.animesachimidv2.domains

import kotlin.math.round
import kotlin.random.Random.Default.nextDouble

data class Recommendation(
    val id: String,
    val slug: String,
    val title: String,
    val imageUrl: String,
    val meta: String,
    val score: Double? = round(nextDouble(from = 0.0, until = 10.0) * 100) / 100,
)



