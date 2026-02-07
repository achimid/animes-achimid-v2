package br.com.achimid.animesachimidv2.domains.dto

import java.time.Instant
import java.util.*

data class AnimeCommentDTO(
    val comments: List<CommentDTO>? = emptyList(),
)

data class CommentDTO(
    val id: UUID? = null,
    val user: String? = "Anonymous",
    val avatar: String? = user?.take(2)?.uppercase() ?: "AA",
    val content: String,
    val date: Instant = Instant.now(),
)