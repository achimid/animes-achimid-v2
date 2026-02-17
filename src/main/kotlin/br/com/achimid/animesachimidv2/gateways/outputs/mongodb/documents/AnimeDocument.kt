package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import br.com.achimid.animesachimidv2.domains.Jikan
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
    val imageUrl: String? = null,
    val type: AnimeTypeDocument = AnimeTypeDocument.TV,
    val status: AnimeStatusDocument = AnimeStatusDocument.COMPLETE,

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
    val popularity: String? = null,
    val rank: String? = null,

    val sources: SourceWrapperDocument? = null,

    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
)

enum class AnimeStatusDocument(val description: String) {
    COMPLETE("Finalizado"),
    AIRING("Em Exibição"),
    WAITING("Aguardando"),
    CANCELLED("Cancelado"),
}

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
    val jikan: Jikan? = null
)