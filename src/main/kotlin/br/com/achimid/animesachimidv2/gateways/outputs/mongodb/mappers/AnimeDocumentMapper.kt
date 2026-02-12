package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.*
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.AnimeStatusDocument.AIRING
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.AnimeStatusDocument.COMPLETE
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.AnimeTypeDocument.TV
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants.ComponentModel.SPRING
import java.time.Instant

@Mapper(componentModel = SPRING)
interface AnimeDocumentMapper {

    fun fromDocument(document: AnimeDocument) : Anime

    fun mapper(old: AnimeOldDocument): AnimeDocument {
        val jikan = old.source!!.jikan!!

        val slug = jikan.url!!.split("/").last().lowercase()

        return AnimeDocument(
            id = jikan.malId.toString(),
            slug = slug,
            name = jikan.title!!,
            type = AnimeTypeDocument.entries.firstOrNull { it.name == jikan.type } ?:TV,
            status = if (jikan.airing == true) AIRING else COMPLETE,
            episodes = emptyList(),
            imageUrl = jikan.images?.webp?.imageUrl ?: jikan.images?.jpg?.imageUrl ?: jikan.images?.webp?.smallImageUrl
            ?: jikan.images?.jpg?.imageUrl!!,
            tags = jikan.genres?.map { it.name }?.filterNotNull(),
            description = jikan.synopsis,
            synopsis = jikan.synopsis,
            background = jikan.background,
            infoList = listOf(
                AnimeDetailsInfoDocument("Source", jikan.source),
                AnimeDetailsInfoDocument("Season", "${jikan.season} ${jikan.year}"),
                AnimeDetailsInfoDocument("Episodes", jikan.episodes.toString()),
                AnimeDetailsInfoDocument("Duration", jikan.duration),
                AnimeDetailsInfoDocument("Studios", jikan.studios?.map { it.name }?.filterNotNull()?.joinToString()),
                AnimeDetailsInfoDocument("Released", jikan.aired?.string),
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
