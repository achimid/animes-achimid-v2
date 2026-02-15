package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.domains.AnimeDetailsInfo
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.*
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.AnimeStatusDocument.AIRING
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.AnimeStatusDocument.COMPLETE
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.AnimeTypeDocument.TV
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

    @Mapping(source = "infoValue", target = "infoValue", defaultValue = "??")
    fun fromDocument(document: AnimeDetailsInfoDocument): AnimeDetailsInfo

    fun toDocument(domain: AnimeComment): AnimeCommentDocument
    fun fromDocument(document: AnimeCommentDocument): AnimeComment

    fun mapper(old: AnimeDocument): AnimeDocument {
        val jikan = old.sources!!.jikan!!

        val slug = jikan.url!!.split("/").last().lowercase()

        return AnimeDocument(
            id = jikan.malId.toString(),
            slug = slug,
            name = jikan.title!!,
            type = AnimeTypeDocument.entries.firstOrNull { it.name == jikan.type } ?: TV,
            status = if (jikan.aired?.to == null) AIRING else COMPLETE,
            episodes = emptyList(),
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
            sources = SourceWrapperDocument(jikan = jikan),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
        )
    }

}
