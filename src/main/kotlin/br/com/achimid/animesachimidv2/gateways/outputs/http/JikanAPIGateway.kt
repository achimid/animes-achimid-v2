package br.com.achimid.animesachimidv2.gateways.outputs.http

import br.com.achimid.animesachimidv2.domains.Jikan
import br.com.achimid.animesachimidv2.gateways.outputs.http.jikan.JikanAPIClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class JikanAPIGateway(
    val jikanAPIClient: JikanAPIClient,
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    fun search(text: String): List<Jikan> {
        logger.info("Searching for anime on Jikan: $text")

        return try {
            jikanAPIClient.searchAnime(text).data
        } catch (ex: RuntimeException) {
            logError(ex)
            emptyList()
        }
    }


    fun findById(id: String): Jikan? {
        logger.info("Find anime on Jikan by id: $id")
        return try {
            jikanAPIClient.findById(id).data
        } catch (ex: RuntimeException) {
            logError(ex)
            null
        }
    }

    fun logError(ex: RuntimeException) {
        if (!ex.message!!.contains("[429 Too Many Requests]")) {
            logger.error("Error on integrate with Jikan", ex)
        } else {
            logger.error("Integrate with Jikan limited")
        }
    }

}