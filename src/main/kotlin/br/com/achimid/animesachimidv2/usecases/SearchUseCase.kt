package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.gateways.outputs.http.JikanAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.NameDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.NamesRepository
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old.AnimeRepository
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

        val scoredPossibilities = FuzzySearch.extractSorted(text, rawPossibilities.map { it.name }).filter { it.score >= 95 }

        if (scoredPossibilities.isNotEmpty()) return rawPossibilities
            .filter { possibility -> scoredPossibilities.any { it.string == possibility.name } }
            .mapNotNull { animeGateway.findById(it.animeId) }

        val animesJikan = jikanAPIGateway.search(text).let(animeGateway::saveAll)

        val scoredPossibilitiesJikan = FuzzySearch.extractSorted(text, animesJikan.map { it.name }).filter { it.score > 97 }

        if (scoredPossibilitiesJikan.isNotEmpty()) return animesJikan
            .filter { possibility -> scoredPossibilitiesJikan.any { it.string == possibility.name } }

        logger.info("No perfect match found, return all matches")

        if (animesJikan.isNotEmpty()) return animesJikan

        return listOf(animeGateway.findById(rawPossibilities.first().animeId)!!)
    }


    //    @PostConstruct
//    fun init() {
//        val names = animeRepository.findAll().toList().flatMap {
//            val jikan = it.sources!!.jikan!!
//
//            val names = mutableSetOf(
//                NameDocument(jikan.title!!, it.id!!),
//                NameDocument(jikan.titleEnglish ?: "", it.id),
//                NameDocument(jikan.titleJapanese ?: "", it.id),
//            )
//
//            println(jikan.titles)
//            names.addAll(jikan.titles?.map { title ->
//                return@map try {
//                    NameDocument((title as HashMap<String, String>).get("title") ?: "", it.id)
//                } catch (ex: Exception) {
//                    NameDocument((title as String ?: ""), it.id)
//                }
//            } ?: emptyList())
//            names.addAll(jikan.titleSynonyms?.map { title -> NameDocument(title, it.id) } ?: emptyList())
//
//            return@flatMap names
//        }.filter { it.name != "" }
//
//        println(names)
//        names.forEach { namesRepository.save(it) }
//    }

}