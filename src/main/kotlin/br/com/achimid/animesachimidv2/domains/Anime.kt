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
    val synopsis: String? = null,
    val background: String? = null,
    val imageBackgroundUrl: String? = null,
    val nameSecondary: String? = null,
    val tags: List<String>? = null,
    val infoList: List<AnimeDetailsInfo>? = null,
    val score: Double? = null,
    val popularity: String? = null,
    val rank: String? = null,
) {

    fun getTypeDescription(): String {
        if (type == "TV") return "Episódio"
        return type
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
) {
    fun date() = createdAt.toString().split("T")[0]
}