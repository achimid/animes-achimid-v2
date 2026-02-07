package br.com.achimid.animesachimidv2.domains.dto

import br.com.achimid.animesachimidv2.domains.dto.AnimeDetailStatusDTO.COMPLETO
import kotlin.math.round
import kotlin.random.Random.Default.nextDouble

data class AnimeDetailDTO(
    val titles: AnimeDetailTitleDTO,
    val status: AnimeDetailStatusDTO = COMPLETO,
    val imageUrl: String,
    val imageBackgroundUrl: String? = "",
    val description: String? = "Sem descrição no momento...",
    val tags: List<String> = emptyList(),
    val synopsis: String? = null,
    val infoList: List<AnimeDetailsInfoDTO>? = null,
    val rankings: RankingStatsDTO,
    val accessCounter: Long? = System.currentTimeMillis(),
) {
    fun getBaseInfo(): List<AnimeDetailsInfoDTO>? {
        val baseInfoNames = listOf("Episodes", "Studio", "Season", "Status")
        return infoList?.filter { baseInfoNames.contains(it.infoName) }
    }
}

data class AnimeDetailTitleDTO(
    val primary: String,
    val english: String? = null,
    val japanese: String? = null,
    val synonyms: List<String>? = emptyList(),
) {
    val secondary : String = english ?: japanese ?: ""
}

enum class AnimeDetailStatusDTO { COMPLETO, EXIBINDO, AGUARDANDO }

data class EpisodeInfoDTO(
    val type: String? = null,
    val number: String? = null,
    val title: String? = null,
    val options: List<EpisodeLinkOptionsDTO>
)

data class EpisodeLinkOptionsDTO(
    val url: String,
    val name: String,
)

data class AnimeDetailsInfoDTO(
    val infoName: String,
    val infoValue: String? = ""
)

data class RankingStatsDTO(
    val rating: Double? = round(nextDouble(from = 0.0, until = 10.0)) / 100,
    val popularity: String? = null,
    val rank: String? = null,
)

