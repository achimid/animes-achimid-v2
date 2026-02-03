package br.com.achimid.animesachimidv2.domains

import br.com.achimid.animesachimidv2.domains.AnimeDetailStatus.COMPLETO
import kotlin.random.Random

data class AnimeDetail(
    val title: String,
    val titleSecondary: String? = null,
    val status: AnimeDetailStatus = COMPLETO,
    val imageUrl: String,
    val imageBackgroundUrl: String? = null,
    val description: String? = "Sem descrição no momento...",
    val tags: List<String> = emptyList(),
    val synopsis: String? = null,
    val episodes: List<EpisodeInfo>? = null,
    val infoList: List<AnimeDetailsInfo>? = null,
    val rankings: RankingStats,
    val accessCounter: Long? = System.currentTimeMillis(),
) {
    val baseInfoNames = listOf("EpisodesCount", "Studio", "Season", "Status")

    fun getBaseInfo(): List<AnimeDetailsInfo>? {
        return infoList?.filter { baseInfoNames.contains(it.infoName) }
    }
}

enum class AnimeDetailStatus { COMPLETO, EXIBINDO, AGUARDANDO }

data class EpisodeInfo(
    val type: String? = null,
    val number: String? = null,
    val title: String? = null,
    val options: List<EpisodeLinkOptions>
)

data class EpisodeLinkOptions(
    val url: String,
    val name: String,
)


data class AnimeDetailsInfo(
    val infoName: String,
    val infoValue: String? = ""
)

data class RankingStats(
    val rating: Double? = Random.nextDouble(from = 0.0, until = 10.0),
    val popularity: String? = null,
    val rank: String? = null,
)

