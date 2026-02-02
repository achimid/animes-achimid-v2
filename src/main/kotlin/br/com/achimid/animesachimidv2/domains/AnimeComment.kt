package br.com.achimid.animesachimidv2.domains

import java.time.Instant
import java.util.*

data class AnimeComment(
    val animeId: UUID,
    val comments: List<Comment>? = emptyList(),
)

data class Comment(
    val id: UUID? = null,
    val user: String? = "Anonymous",
    val avatar: String? = user?.take(2)?.uppercase() ?: "AA",
    val content: String,
    val date: Instant = Instant.now(),
)