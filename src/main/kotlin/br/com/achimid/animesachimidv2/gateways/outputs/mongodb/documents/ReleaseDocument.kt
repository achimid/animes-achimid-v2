package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import lombok.Data
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "releases")
data class ReleaseDocument(
    @Id
    val id: String? = null,

    val title: String? = null,
    val episode: String? = null,

    val animeId: String? = null,
    val animeSlug: String? = null,
    val animeName: String? = null,
    val animeType: String? = null,
    val animeImage: String? = null,
    val animeEpisode: String? = null,
    val animeStreamUrl: String? = null,

    @Transient
    val anime: ReleaseAnimeDocument? = null,
    val mirrors: List<MirrorDocument>? = emptyList(),
    val sources: List<ReleaseSourceDocument>? = emptyList(),

    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
)

data class ReleaseSourceDocument(
    val title: String,
    val url: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReleaseSourceDocument

        if (title != other.title) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + url.hashCode()
        return result
    }
}

data class ReleaseAnimeDocument(
    val _id: String? = null,
    val name: String? = null,
    val type: String? = null,
    val image: String? = null,
    val source: SourceWrapperDocument
)
