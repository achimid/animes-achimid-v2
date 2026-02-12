package br.com.achimid.animesachimidv2.domains

data class Anime(
    val id: String,
    val slug: String,
    val name: String,
    val type: String,
    val status: String,
    val imageUrl: String,

    val episodes: List<EpisodeInfo>? = null,
    val accessCounter: Long = 0,

    val description: String? = null,
    val synopsis: String? = null,
    val background: String? = null,
    val imageBackgroundUrl: String? = null,
    val nameSecondary: String? = null,
    val tags: List<String>? = null,
    val infoList: List<AnimeDetailsInfo>? = null,
    val score: Double? = 0.1,
    val popularity: String? = "??",
    val rank: String? = "??",
) {

    fun getScore() { score ?: 0.1 }

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
)

data class EpisodeLinkOptions(
    val url: String,
    val name: String,
)

data class AnimeDetailsInfo(
    val infoName: String,
    val infoValue: String? = null
)
