package br.com.achimid.animesachimidv2.gateways.outputs.http.anilist

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

/**
 * Cliente da AniList (GraphQL) — fonte adicional de enriquecimento (FUNC-13).
 * Usado para obter o próximo episódio em exibição (countdown).
 */
@FeignClient(name = "anilist", url = "\${external.anilist.url}")
interface AniListAPIClient {

    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun query(@RequestBody request: AniListRequest): AniListResponse
}

data class AniListRequest(
    val query: String,
    val variables: Map<String, Any?>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AniListResponse(
    val data: AniListData? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AniListData(
    @JsonProperty("Media") val media: AniListMedia? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AniListMedia(
    val nextAiringEpisode: AniListNextAiring? = null,
    val genres: List<String>? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AniListNextAiring(
    val episode: Int? = null,
    val airingAt: Long? = null,
    val timeUntilAiring: Long? = null,
)
