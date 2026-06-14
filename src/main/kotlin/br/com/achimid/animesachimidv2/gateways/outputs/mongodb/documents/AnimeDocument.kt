package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import br.com.achimid.animesachimidv2.domains.CommentStatus
import br.com.achimid.animesachimidv2.domains.Jikan
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeStatusDocument.COMPLETE
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeTypeDocument.TV
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "animes")
@CompoundIndex(name = "comments_status_idx", def = "{'comments.status': 1}")
data class AnimeDocument(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val slug: String,
    @Indexed
    val name: String,
    val imageUrl: String? = null,
    val type: AnimeTypeDocument = TV,
    val status: AnimeStatusDocument = COMPLETE,

    val accessCounter: Long? = null,

    val episodes: List<EpisodeInfoDocument>? = null,
    val comments: List<AnimeCommentDocument>? = null,

    val description: String? = null,
    var descriptionPtBr: String? = null,
    val synopsis: String? = null,
    var synopsisPtBr: String? = null,
    val background: String? = null,
    val episodesCount: Int? = null,
    val imageBackgroundUrl: String? = null,
    val nameSecondary: String? = null,
    @Indexed
    val tags: List<String>? = null,
    val infoList: List<AnimeDetailsInfoDocument>? = null,
    @Indexed
    val score: Double? = null,
    val popularity: String? = null,
    val rank: String? = null,
    val streamingUrl: String? = null,

    val sources: SourceWrapperDocument? = null,

    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    @Indexed
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
    // Default APPROVED para que comentários antigos (sem o campo) continuem aparecendo (FUNC-05).
    val status: CommentStatus = CommentStatus.APPROVED,
)


data class SourceWrapperDocument(
    val jikan: Jikan? = null
)