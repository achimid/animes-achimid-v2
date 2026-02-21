package br.com.achimid.animesachimidv2.domains

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

data class JikanApiResponse(
    val data: List<Jikan>
)

data class JikanApiSingleResponse(
    val data: Jikan
)


data class Jikan(
    @Field("mal_id")
    @JsonProperty("mal_id")
    val malId: Int,
    val url: String,
    val title: String,
    val images: JikanAnimeImages? = null,
    val trailer: JikanTrailer? = null,
    val approved: Boolean? = null,
    val titles: List<Any>? = null,
    @Field("title_english")
    @JsonProperty("title_english")
    val titleEnglish: String? = null,
    @Field("title_japanese")
    @JsonProperty("title_japanese")
    val titleJapanese: String? = null,
    @Field("title_synonyms")
    @JsonProperty("title_synonyms")
    val titleSynonyms: List<String>? = null,
    val type: String? = null,
    val source: String? = null,
    val episodes: Int? = null,
    val status: String? = null,
    val airing: Boolean? = null,
    val aired: JikanAiredDate? = null,
    val duration: String? = null,
    val rating: String? = null,
    val score: Double? = null,
    @Field("scored_by")
    @JsonProperty("scored_by")
    val scoredBy: Int? = null,
    val rank: String? = null,
    val popularity: String? = null,
    val members: Int? = null,
    val favorites: Int? = null,
    val synopsis: String? = null,
    val background: String? = null,
    val season: String? = null,
    val year: Int? = null,
    val broadcast: JikanBroadcast? = null,
    val producers: List<JikanEntityInfo>? = null,
    val licensors: List<JikanEntityInfo>? = null,
    val studios: List<JikanEntityInfo>? = null,
    val genres: List<JikanEntityInfo>? = null,
    @Field("explicit_genres")
    @JsonProperty("explicit_genres")
    val explicitGenres: List<JikanEntityInfo>? = null,
    val themes: List<JikanEntityInfo>? = null,
    val demographics: List<JikanEntityInfo>? = null,
    val relations: List<JikanRelation>? = null,
    val theme: JikanThemeMusic? = null,
    val external: List<JikanResourceLink>? = null,
    val streaming: List<JikanResourceLink>? = null,
)

data class JikanRelation(
    val relation: String? = null,
    val entry: List<JikanEntityInfo>? = null
)

data class JikanThemeMusic(
    val openings: List<String>? = null,
    val endings: List<String>? = null
)

data class JikanResourceLink(
    val name: String? = null,
    val url: String? = null
)

data class JikanAnimeImages(
    val jpg: JikanImageVariant? = null,
    val webp: JikanImageVariant? = null,
)

data class JikanImageVariant(
    @Field("image_url") @JsonProperty("image_url") val imageUrl: String? = null,
    @Field("small_image_url") @JsonProperty("small_image_url") val smallImageUrl: String? = null,
    @Field("large_image_url") @JsonProperty("large_image_url") val largeImageUrl: String? = null,
)

data class JikanTrailer(
    @Field("youtube_id") @JsonProperty("youtube_id") val youtubeId: String? = null,
    val url: String? = null,
    @Field("embed_url") @JsonProperty("embed_url") val embedUrl: String? = null,
    val images: JikanTrailerImages? = null,
)

data class JikanTrailerImages(
    @Field("image_url") @JsonProperty("image_url") val imageUrl: String? = null,
    @Field("small_image_url") @JsonProperty("small_image_url") val smallImageUrl: String? = null,
    @Field("medium_image_url") @JsonProperty("medium_image_url") val mediumImageUrl: String? = null,
    @Field("large_image_url") @JsonProperty("large_image_url") val largeImageUrl: String? = null,
    @Field("maximum_image_url") @JsonProperty("maximum_image_url") val maximumImageUrl: String? = null,
)

data class JikanTitleEntry(
    val type: String? = null,
    val title: String? = null,
)

data class JikanAiredDate(
    val from: String? = null,
    val to: String? = null,
    val prop: JikanAiredProp? = null,
    val string: String? = null,
)

data class JikanAiredProp(
    val from: JikanDateDetail? = null,
    val to: JikanDateDetail? = null,
)

data class JikanDateDetail(
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = null,
)

data class JikanBroadcast(
    val day: String? = null,
    val time: String? = null,
    val timezone: String? = null,
    val string: String? = null,
)

data class JikanEntityInfo(
    @Field("mal_id")
    @JsonProperty("mal_id")
    val malId: Int? = null,
    val type: String? = null,
    val name: String? = null,
    val url: String? = null,
)