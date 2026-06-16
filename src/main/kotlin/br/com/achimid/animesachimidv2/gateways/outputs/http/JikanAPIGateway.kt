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

    fun findCurrentSeason(): List<Jikan> {
        logger.info("Fetching current season from Jikan /seasons/now")
        val result = mutableListOf<Jikan>()
        var page = 1
        var response = runCatching { jikanAPIClient.findSeasonNow(page) }.getOrElse { return emptyList() }
        result.addAll(response.data)
        while (response.pagination?.hasNextPage == true && page < 5) {
            page++
            Thread.sleep(400)
            response = runCatching { jikanAPIClient.findSeasonNow(page) }.getOrNull() ?: break
            result.addAll(response.data)
        }
        logger.info("Jikan /seasons/now returned ${result.size} animes (${page} page(s))")
        return result
    }

    fun logError(ex: RuntimeException) {
        val message = ex.message ?: ""
        when {
            message.contains("[404 Not Found]") -> logger.warn("Jikan returned 404: $message")
            message.contains("[429 Too Many Requests]") -> logger.warn("Jikan rate limit reached")
            else -> logger.error("Error on integrate with Jikan", ex)
        }
    }

}