package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.gateways.outputs.http.TranslateAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import kotlin.time.measureTimedValue

@Component
class TranslateAnimeInfoUserCase(
    private val animeGateway: AnimeGateway,
    private val translateGateway: TranslateAPIGateway
) {
    val logger = LoggerFactory.getLogger(this.javaClass)

    @EventListener(ApplicationReadyEvent::class, condition = "'\${spring.profiles.active}' == 'prod'")
    fun processAnimeWithoutTranslation() {
        val animeWithoutTranslation = animeGateway.findAllWithoutTranslation()

        logger.info("Processing animes without translation: ${animeWithoutTranslation.size}")

        animeWithoutTranslation.forEach { execute(it) }
    }

    fun execute(anime: Anime) {
        logger.info("Processing anime translation: ${anime.name}")

        if (anime.synopsisPtBr == null) anime.synopsisPtBr = translateText(anime.synopsis)
        if (anime.descriptionPtBr == null) anime.descriptionPtBr = translateText(anime.description)

        animeGateway.save(anime)
    }

    private fun translateText(text: String?): String? {
        if (text.isNullOrEmpty()) return text

        val (textTranslated, duration)  = measureTimedValue { translateGateway.translate(text) }
        logger.info("Text translation took ${duration.inWholeMilliseconds} ms")

        return textTranslated
    }


}