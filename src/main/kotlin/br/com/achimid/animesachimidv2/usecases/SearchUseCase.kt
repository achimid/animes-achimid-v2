package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.SearchResult
import br.com.achimid.animesachimidv2.gateways.outputs.http.JikanAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.NameDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.NamesRepository
import br.com.achimid.animesachimidv2.utils.normalizeTitle
import br.com.achimid.animesachimidv2.utils.stripSeasonFromTitle
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

private const val SCORE_CONFIDENT = 90
private const val SCORE_UNCERTAIN = 70
private const val SCORE_LOCAL_THRESHOLD = 97
private const val SCORE_JIKAN_THRESHOLD = 95

@Component
class SearchUseCase(
    val animeGateway: AnimeGateway,
    val namesRepository: NamesRepository,
    val jikanAPIGateway: JikanAPIGateway,
) {

    val logger = LoggerFactory.getLogger(SearchUseCase::class.java)

    @Cacheable("calendarSearchCache")
    fun execute(text: String): SearchResult {
        val normalized = normalizeTitle(text)

        // Passe 0 — alias confirmado (prioridade máxima)
        val confirmedAlias = namesRepository.findConfirmedAlias(normalized)
            ?: namesRepository.findConfirmedAlias(text)
        if (confirmedAlias != null) {
            val anime = animeGateway.findById(confirmedAlias.animeId)
            if (anime != null) {
                logger.info("Search: '$text' → alias confirmado → match: ${anime.name}")
                return SearchResult(anime, 100, false, text)
            }
        }

        // Passe 1 — título normalizado com formato de temporada padrão
        val result1 = tryMatch(normalized, text, pass = 1)
        if (result1 != null && result1.score >= SCORE_UNCERTAIN) return result1

        // Passe 2 — título sem informação de temporada (último recurso, sempre needsReview)
        val stripped = stripSeasonFromTitle(text)
        if (stripped != normalized && stripped.isNotBlank()) {
            val result2 = tryMatch(stripped, text, pass = 2)
            if (result2 != null) {
                logger.info("Search: '$text' → passe 2 (sem temporada) → score: ${result2.score} → match: ${result2.anime.name}")
                return result2.copy(needsReview = true)
            }
        }

        // Fallback final — retorna o primeiro resultado local sem score confiável
        val fallback = namesRepository.searchByProximity(normalized).firstOrNull()
        if (fallback != null) {
            val anime = animeGateway.findById(fallback.animeId)
            if (anime != null) {
                logger.warn("Search: '$text' → fallback sem score confiável → match: ${anime.name}")
                return SearchResult(anime, 0, true, text)
            }
        }

        // Último recurso absoluto — busca no Jikan
        val jikanResults = jikanAPIGateway.search(text).let(animeGateway::saveAll)
        val best = jikanResults.firstOrNull()
        return if (best != null) {
            logger.warn("Search: '$text' → fallback Jikan → match: ${best.name}")
            SearchResult(best, 0, true, text)
        } else {
            throw IllegalStateException("Nenhum anime encontrado para: $text")
        }
    }

    private fun tryMatch(query: String, rawTitle: String, pass: Int): SearchResult? {
        val rawPossibilities = namesRepository.searchByProximity(query)
        if (rawPossibilities.isEmpty()) {
            return tryJikanMatch(query, rawTitle, pass)
        }

        val names = rawPossibilities.map { it.name }
        val best = FuzzySearch.extractOne(query, names)

        if (best != null && best.score >= SCORE_LOCAL_THRESHOLD) {
            val doc = rawPossibilities.first { it.name == best.string }
            val anime = animeGateway.findById(doc.animeId) ?: return null
            val needsReview = best.score < SCORE_CONFIDENT
            logger.info("Search: '$rawTitle' → passe $pass (local) → score: ${best.score} → match: ${anime.name}")
            return SearchResult(anime, best.score, needsReview, rawTitle)
        }

        // Score local insuficiente — tenta Jikan
        val jikanResult = tryJikanMatch(query, rawTitle, pass)
        if (jikanResult != null) return jikanResult

        // Aceita score local abaixo do threshold como incerto
        if (best != null && best.score >= SCORE_UNCERTAIN) {
            val doc = rawPossibilities.first { it.name == best.string }
            val anime = animeGateway.findById(doc.animeId) ?: return null
            logger.info("Search: '$rawTitle' → passe $pass (local baixo) → score: ${best.score} → match: ${anime.name}")
            return SearchResult(anime, best.score, true, rawTitle)
        }

        return null
    }

    private fun tryJikanMatch(query: String, rawTitle: String, pass: Int): SearchResult? {
        val animesJikan = runCatching { jikanAPIGateway.search(query).let(animeGateway::saveAll) }.getOrDefault(emptyList())
        if (animesJikan.isEmpty()) return null

        val names = animesJikan.map { it.name }
        val best = FuzzySearch.extractOne(query, names) ?: return null

        if (best.score >= SCORE_JIKAN_THRESHOLD) {
            val anime = animesJikan.first { it.name == best.string }
            saveNameAsPossibility(rawTitle, anime)
            val needsReview = best.score < SCORE_CONFIDENT
            logger.info("Search: '$rawTitle' → passe $pass (Jikan) → score: ${best.score} → match: ${anime.name}")
            return SearchResult(anime, best.score, needsReview, rawTitle)
        }

        return null
    }

    fun saveNameAsPossibility(text: String, anime: Anime): Anime {
        namesRepository.save(NameDocument(name = text, animeId = anime.id, potential = true, animeName = anime.name))
        return anime
    }

}
