package br.com.achimid.animesachimidv2.cron

import br.com.achimid.animesachimidv2.gateways.outputs.http.PuppeteerAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.SiteIntegrationGateway
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ExtractionTask(
    val puppeteerAPIGateway: PuppeteerAPIGateway,
    val siteIntegrationGateway: SiteIntegrationGateway,
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedRate = 1000 * 60 * 5)
    fun executeFastQueueMonitoring() {
        logger.info("Executing fast queue monitoring")

        siteIntegrationGateway.findFast().forEach(puppeteerAPIGateway::execute)
    }

//    @Scheduled(fixedRate = 1000 * 60 * 7)
    fun executeMediumQueueMonitoring() {
        logger.info("Executing medium queue monitoring")

        siteIntegrationGateway.findMedium().forEach(puppeteerAPIGateway::execute)
    }

    @Scheduled(fixedRate = 1000 * 60 * 12)
    fun executeSlowQueueMonitoring() {
        logger.info("Executing slow queue monitoring")

        siteIntegrationGateway.findSlow().forEach(puppeteerAPIGateway::execute)
    }

}