package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.Recommendation
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.springframework.stereotype.Component

/**
 * Recomendações por conteúdo (FUNC-10): em vez de aleatório puro, pontua candidatos pela
 * sobreposição de gêneros/temas (tags). Na home usa o perfil do usuário (gêneros dos favoritos);
 * no detalhe usa os gêneros do próprio anime. Sem sinal suficiente, cai no aleatório (fallback).
 */
@Component
class FindRecommendationsUseCase(
    private val animeGateway: AnimeGateway,
    private val findFavoriteAnimesUseCase: FindFavoriteAnimesUseCase,
) {

    private val candidatePoolSize = 60

    /** Recomendações para a home, personalizadas pelos gêneros dos favoritos do usuário. */
    fun execute(size: Int = 5, userId: String? = null): List<Recommendation> {
        val favorites = userId?.let { findFavoriteAnimesUseCase.execute(it) }.orEmpty()
        val profileTags = favorites.flatMap { it.tags.orEmpty() }
        if (profileTags.isEmpty()) return randomRecommendations(size)

        val favoriteIds = favorites.map { it.id }.toSet()
        return rankByTags(animeGateway.findByTags(profileTags, candidatePoolSize), profileTags, favoriteIds, size)
            .ifEmpty { randomRecommendations(size) }
    }

    /** Recomendações "animes parecidos", pelos gêneros do anime atual (página de detalhe). */
    fun forAnime(anime: Anime, size: Int = 3): List<Recommendation> {
        val tags = anime.tags.orEmpty()
        if (tags.isEmpty()) return randomRecommendations(size)

        return rankByTags(animeGateway.findByTags(tags, candidatePoolSize), tags, setOf(anime.id), size)
            .ifEmpty { randomRecommendations(size) }
    }

    private fun rankByTags(
        candidates: List<Anime>,
        referenceTags: List<String>,
        excludeIds: Set<String>,
        size: Int
    ): List<Recommendation> {
        val reference = referenceTags.toSet()
        return candidates
            .asSequence()
            .filter { it.id !in excludeIds }
            .distinctBy { it.id }
            .sortedWith(
                compareByDescending<Anime> { anime -> anime.tags.orEmpty().count { it in reference } }
                    .thenByDescending { it.score ?: 0.0 }
            )
            .take(size)
            .map(::mapper)
            .toList()
    }

    private fun randomRecommendations(size: Int): List<Recommendation> =
        animeGateway.findRandom(size).map(::mapper)

    fun mapper(anime: Anime): Recommendation = Recommendation(
        id = anime.id,
        slug = anime.slug,
        title = anime.name,
        imageUrl = anime.imageUrl,
        meta = anime.tags?.joinToString("/") ?: "",
        score = anime.score ?: 0.0
    )
}
