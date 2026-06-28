package br.com.achimid.animesachimidv2.domains

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant
import java.util.*

data class Anime(
    val id: String,
    val slug: String,
    val name: String,
    val type: String,
    val status: String,
    val imageUrl: String,

    val accessCounter: Long = 0,

    @JsonIgnore
    val episodes: List<EpisodeInfo>? = null,
    @JsonIgnore
    val comments: List<AnimeComment>? = null,

    val description: String? = null,
    var descriptionPtBr: String? = null,
    val synopsis: String? = null,
    var synopsisPtBr: String? = null,
    val background: String? = null,
    val imageBackgroundUrl: String? = null,
    val nameSecondary: String? = null,
    val tags: List<String>? = null,
    val infoList: List<AnimeDetailsInfo>? = null,
    val score: Double? = null,
    val scoredBy: Int? = null,
    val popularity: String? = null,
    val rank: String? = null,
    val streamingUrl: String? = null,
    val jikanSyncedAt: Instant? = null,
    val createdAt: Instant? = null,
    val episodesCount: Int? = null,
    val season: String? = null,
    val year: Int? = null,
) {

    fun getTypeDescription(): String {
        if (type == "TV") return "Episódio"
        return type
    }

    fun getDescriptionTranslated(): String? {
        if (descriptionPtBr.isNullOrEmpty()) return description
        return descriptionPtBr
    }

    fun getSynopsisTranslated(): String? {
        if (synopsisPtBr.isNullOrEmpty()) return synopsis
        return synopsisPtBr
    }

    fun createdAtFormatted(): String? = createdAt?.toString()?.substring(0, 10)
    fun jikanSyncedAtFormatted(): String? = jikanSyncedAt?.toString()?.let {
        val d = it.substring(0, 10)
        val t = it.substring(11, 16)
        "$d $t"
    }
}

data class EpisodeInfo(
    val number: String? = null,
    val title: String? = null,
    val type: String? = null,
    val options: List<EpisodeLinkOptions>? = null,
) {
    fun getTypeDescription(): String? {
        if (type == "TV") return "Episódio"
        return type
    }
}

data class EpisodeLinkOptions(
    val url: String,
    val name: String,
)

data class AnimeDetailsInfo(
    val infoName: String,
    val infoValue: String? = null
)

data class AnimeComment(
    val id: String? = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String? = "Anonymous",
    val avatar: String? = userName?.take(2)?.uppercase() ?: "AA",
    val content: String,
    val createdAt: Instant = Instant.now(),
    val status: CommentStatus = CommentStatus.PENDING,
) {
    fun date() = createdAt.toString().split("T")[0]
    fun isApproved() = status == CommentStatus.APPROVED
}

enum class CommentStatus { PENDING, APPROVED, REJECTED }