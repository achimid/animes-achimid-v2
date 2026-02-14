package br.com.achimid.animesachimidv2.gateways.outputs.http

import br.com.achimid.animesachimidv2.domains.SiteIntegration
import br.com.achimid.animesachimidv2.gateways.outputs.http.puppeteer.PuppeteerAPIClient
import br.com.achimid.animesachimidv2.gateways.outputs.http.puppeteer.request.ExecutionConfig
import br.com.achimid.animesachimidv2.gateways.outputs.http.puppeteer.request.ExecutionRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PuppeteerAPIGateway(
    val puppeteerAPIClient: PuppeteerAPIClient
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(siteIntegration: SiteIntegration) {
        logger.info("Executing puppeteer API for site: ${siteIntegration.name}")

        puppeteerAPIClient.execute(
            ExecutionRequest(
                url = siteIntegration.url,
                script = siteIntegration.script!!,
                callbackUrl = "https://local.achimid.com.br/api/v1/site/integration/callback",
                ref = siteIntegration.name,
                config = ExecutionConfig(true, siteIntegration.skipImage)
            )
        ).let { logger.info("Executed puppeteer API for site ${siteIntegration.name}") }
    }

}