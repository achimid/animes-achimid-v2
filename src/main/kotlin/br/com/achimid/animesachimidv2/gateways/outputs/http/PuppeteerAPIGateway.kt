package br.com.achimid.animesachimidv2.gateways.outputs.http

import br.com.achimid.animesachimidv2.domains.SiteIntegration
import br.com.achimid.animesachimidv2.gateways.outputs.http.puppeteer.PuppeteerAPIClient
import br.com.achimid.animesachimidv2.gateways.outputs.http.puppeteer.request.ExecutionConfig
import br.com.achimid.animesachimidv2.gateways.outputs.http.puppeteer.request.ExecutionRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PuppeteerAPIGateway(
    val puppeteerAPIClient: PuppeteerAPIClient,
    @Value("\${integration.callbackUrl}") private val callbackUrl: String,
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(siteIntegration: SiteIntegration) {
        logger.info("Executing puppeteer API for site: ${siteIntegration.name}")

        puppeteerAPIClient.execute(
            ExecutionRequest(
                url = siteIntegration.url,
                script = siteIntegration.script!!,
                callbackUrl = callbackUrl,
                ref = siteIntegration.name,
                config = ExecutionConfig(
                    bypassCSP = true,
                    skipImage = siteIntegration.skipImage,
                    disableJavaScript = siteIntegration.disableJavaScript
                )
            )
        ).let { logger.info("Executed puppeteer API for site ${siteIntegration.name}") }
    }

}