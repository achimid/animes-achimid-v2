package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.gateways.outputs.http.JikanAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.NamesRepository
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SearchUseCase(
    val animeGateway: AnimeGateway,
    val namesRepository: NamesRepository,
    val jikanAPIGateway: JikanAPIGateway,
) {

    val logger = LoggerFactory.getLogger(SearchUseCase::class.java)

    fun execute(text: String): List<Anime> {
        val rawPossibilities = namesRepository.searchByProximity(text)

        val scoredPossibilities =
            FuzzySearch.extractSorted(text, rawPossibilities.map { it.name }).filter { it.score >= 95 }

        if (scoredPossibilities.isNotEmpty()) return rawPossibilities
            .filter { possibility -> scoredPossibilities.any { it.string == possibility.name } }
            .mapNotNull { animeGateway.findById(it.animeId) }

        val animesJikan = jikanAPIGateway.search(text).let(animeGateway::saveAll)

        val scoredPossibilitiesJikan =
            FuzzySearch.extractSorted(text, animesJikan.map { it.name }).filter { it.score > 97 }

        if (scoredPossibilitiesJikan.isNotEmpty()) return animesJikan
            .filter { possibility -> scoredPossibilitiesJikan.any { it.string == possibility.name } }

        logger.info("No perfect match found, return all matches")

        if (animesJikan.isNotEmpty()) return animesJikan

        return listOf(animeGateway.findById(rawPossibilities.first().animeId)!!)
    }

}