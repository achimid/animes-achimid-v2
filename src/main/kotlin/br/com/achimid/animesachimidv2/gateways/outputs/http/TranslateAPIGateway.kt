package br.com.achimid.animesachimidv2.gateways.outputs.http

import br.com.achimid.animesachimidv2.gateways.outputs.http.libretranslate.LibreTranslateAPIClient
import br.com.achimid.animesachimidv2.gateways.outputs.http.libretranslate.request.TranslateRequest
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class TranslateAPIGateway(
    val translateAPIClient: LibreTranslateAPIClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    @Cacheable("translateCache")
    fun translate(text: String): String? {
        logger.info("Executing translate API for text: $text")

        return try {
            translateAPIClient.translate(TranslateRequest(q = text)).translatedText
        } catch (ex: RuntimeException) {
            logger.error("Error on integrate with LibreTranslateAPI", ex)
            return null
        }
    }
}