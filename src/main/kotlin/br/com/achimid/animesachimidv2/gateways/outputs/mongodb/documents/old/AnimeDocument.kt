package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.AnimeStatusDocument.COMPLETE
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.AnimeTypeDocument.TV
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document(collection = "animes")
data class AnimeDocument(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val slug: String,
    val name: String,
    val imageUrl: String,
    val type: AnimeTypeDocument = TV,
    val status: AnimeStatusDocument = COMPLETE,

    val accessCounter: Long? = null,

    val episodes: List<EpisodeInfoDocument>? = null,
    val comments: List<AnimeCommentDocument>? = null,

    val description: String? = null,
    val synopsis: String? = null,
    val background: String? = null,
    val imageBackgroundUrl: String? = null,
    val nameSecondary: String? = null,
    val tags: List<String>? = null,
    val infoList: List<AnimeDetailsInfoDocument>? = null,
    val score: Double? = null,
    val popularity: Int? = null,
    val rank: Int? = null,

    val sources: SourceWrapperDocument? = null,

    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
)

enum class AnimeStatusDocument { COMPLETE, AIRING, WAITING, CANCELLED }
enum class AnimeTypeDocument { TV, OVA, ONA, Movie, Music, Special, OTHERS }


data class EpisodeInfoDocument(
    val number: String? = null,
    val title: String? = null,
    val options: List<EpisodeLinkOptionsDocument>? = null,
)

data class EpisodeLinkOptionsDocument(
    val url: String,
    val name: String,
)

data class AnimeDetailsInfoDocument(
    val infoName: String,
    val infoValue: String? = null
)

data class AnimeCommentDocument(
    val id: String,
    val userId: String,
    val userName: String,
    val avatar: String? = null,
    val content: String,
    val createdAt: Instant,
)



data class SourceWrapperDocument(
    val jikan: JikanDataDocument? = null
)

data class JikanDataDocument(
    @Field("mal_id") val malId: Int? = null,
    val url: String? = null,
    val images: JikanImagesDocument? = null,
    val trailer: JikanTrailerDocument? = null,
    val approved: Boolean? = null,
    val titles: List<Any>? = null,
    val title: String? = null,
    @Field("title_english") val titleEnglish: String? = null,
    @Field("title_japanese") val titleJapanese: String? = null,
    @Field("title_synonyms") val titleSynonyms: List<String>? = null,
    val episodes: Int? = null,
    val status: String? = null,
    val airing: Boolean? = null,
    val aired: JikanAiredInfoDocument? = null,
    val duration: String? = null,
    val rating: String? = null,
    val type: String? = null,
    val source: String? = null,
    val score: Double? = null,
    @Field("scored_by") val scoredBy: Int? = null,
    val rank: Int? = null,
    val popularity: Int? = null,
    val background: String? = null,
    val season: String? = null,
    val year: Int? = null,
    val members: Int? = null,
    val favorites: Int? = null,
    val synopsis: String? = null,
    val producers: List<JikanEntityDocument>? = null,
    val licensors: List<JikanEntityDocument>? = null,
    val studios: List<JikanEntityDocument>? = null,
    val genres: List<JikanEntityDocument>? = null,
    val themes: List<JikanEntityDocument>? = null,
)

data class JikanImagesDocument(
    val jpg: JikanImageUrlsDocument? = null,
    val webp: JikanImageUrlsDocument? = null,
)

data class JikanImageUrlsDocument(
    @Field("image_url") val imageUrl: String? = null,
    @Field("small_image_url") val smallImageUrl: String? = null,
    @Field("large_image_url") val largeImageUrl: String? = null,
)

data class JikanTrailerDocument(
    @Field("youtube_id") val youtubeId: String? = null,
    val url: String? = null,
    @Field("embed_url") val embedUrl: String? = null,
    val images: JikanTrailerImagesDocument? = null,
)

data class JikanTrailerImagesDocument(
    @Field("image_url") val imageUrl: String? = null,
    @Field("small_image_url") val smallImageUrl: String? = null,
    @Field("medium_image_url") val mediumImageUrl: String? = null,
    @Field("large_image_url") val largeImageUrl: String? = null,
    @Field("maximum_image_url") val maximumImageUrl: String? = null,
)

data class JikanAiredInfoDocument(
    val from: String? = null,
    val to: String? = null,
    val prop: JikanAiredPropDocument? = null,
    val string: String? = null,
)

data class JikanAiredPropDocument(
    val from: JikanDateDetailDocument? = null,
    val to: JikanDateDetailDocument? = null,
)

data class JikanDateDetailDocument(
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = null,
)

data class JikanEntityDocument(
    @Field("mal_id") val malId: Int? = null,
    val type: String? = null,
    val name: String? = null,
    val url: String? = null,
)