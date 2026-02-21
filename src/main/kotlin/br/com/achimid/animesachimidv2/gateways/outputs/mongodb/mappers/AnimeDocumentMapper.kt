package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.domains.AnimeDetailsInfo
import br.com.achimid.animesachimidv2.domains.Jikan
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.*
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeStatusDocument.AIRING
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeStatusDocument.COMPLETE
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.AnimeTypeDocument.TV
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants.ComponentModel.SPRING
import java.time.Instant

@Mapper(componentModel = SPRING)
interface AnimeDocumentMapper {

    @Mapping(source = "description", target = "description", defaultValue = "Sem descrição no momento")
    @Mapping(source = "synopsis", target = "synopsis", defaultValue = "Sem descrição no momento")
    @Mapping(source = "rank", target = "rank", defaultValue = "??")
    @Mapping(source = "popularity", target = "popularity", defaultValue = "??")
    @Mapping(source = "score", target = "score", defaultValue = "6.8")
    @Mapping(source = "status.description", target = "status")
    fun fromDocument(document: AnimeDocument): Anime

    fun toDocument(domain: Anime): AnimeDocument

    @Mapping(source = "infoValue", target = "infoValue", defaultValue = "??")
    fun fromDocument(document: AnimeDetailsInfoDocument): AnimeDetailsInfo

    fun toDocument(domain: AnimeComment): AnimeCommentDocument
    fun fromDocument(document: AnimeCommentDocument): AnimeComment

    fun toDomain(jikan: Jikan): Anime = toDocument(jikan).let(this::fromDocument)

    fun toDocument(jikan: Jikan): AnimeDocument {
        val slug = jikan.url.split("/").last().lowercase()

        return AnimeDocument(
            id = jikan.malId.toString(),
            slug = slug,
            name = jikan.title,
            episodes = emptyList(),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
        ).let { merge(it, jikan) }
    }

    fun merge(anime: AnimeDocument, jikan: Jikan): AnimeDocument {
        return anime.copy(
            type = AnimeTypeDocument.entries.firstOrNull { it.name == jikan.type } ?: TV,
            status = if (jikan.aired?.to == null) AIRING else COMPLETE,
            episodesCount = jikan.episodes,
            imageUrl = jikan.images?.webp?.imageUrl
                ?: jikan.images?.jpg?.imageUrl
                ?: jikan.images?.webp?.smallImageUrl
                ?: jikan.images?.jpg?.imageUrl
                ?: jikan.images?.jpg?.smallImageUrl
                ?: jikan.images?.webp?.largeImageUrl
                ?: jikan.images?.jpg?.largeImageUrl,
            imageBackgroundUrl = jikan.images?.webp?.largeImageUrl ?: jikan.images?.jpg?.largeImageUrl,
            tags = jikan.genres?.mapNotNull { it.name },
            description = jikan.synopsis,
            synopsis = jikan.synopsis,
            background = jikan.background,
            infoList = listOf(
                AnimeDetailsInfoDocument("Origem", jikan.source),
                AnimeDetailsInfoDocument("Temporada", jikan.season),
                AnimeDetailsInfoDocument("Lançamento", jikan.aired?.string),
                AnimeDetailsInfoDocument("Episódios", jikan.episodes?.toString()),
                AnimeDetailsInfoDocument("Duração", jikan.duration),
                AnimeDetailsInfoDocument("Estúdio", jikan.studios?.mapNotNull { it.name }?.joinToString()),
                AnimeDetailsInfoDocument("Ano", jikan.year?.toString()),
            ),
            rank = jikan.rank,
            score = jikan.score,
            popularity = jikan.popularity,
            streamingUrl = jikan.streaming?.firstOrNull()?.url,
            sources = SourceWrapperDocument(jikan = jikan),
            updatedAt = Instant.now(),
        )
    }

}
