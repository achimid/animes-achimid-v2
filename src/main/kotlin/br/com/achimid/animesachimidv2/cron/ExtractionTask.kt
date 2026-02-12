package br.com.achimid.animesachimidv2.cron

import br.com.achimid.animesachimidv2.gateways.outputs.http.PuppeteerAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.SiteIntegrationGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old.AnimeOldRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ExtractionTask(
    val puppeteerAPIGateway: PuppeteerAPIGateway,
    val siteIntegrationGateway: SiteIntegrationGateway,
    val repositoryOld: AnimeOldRepository
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedRate = 1000 * 60 * 3)
    fun executeFastQueueMonitoring() {
        logger.info("Executing fast queue monitoring")

//        siteIntegrationGateway.findFast().forEach(puppeteerAPIGateway::execute)
//        val teste = repositoryOld.findAll(PageRequest.of(0, 10))
//        println(teste)
    }

//    @Scheduled(fixedRate = 1000 * 60 * 7)
    fun executeMediumQueueMonitoring() {
        logger.info("Executing medium queue monitoring")

        siteIntegrationGateway.findMedium().forEach(puppeteerAPIGateway::execute)
    }

//    @Scheduled(fixedRate = 1000 * 60 * 12)
    fun executeSlowQueueMonitoring() {
        logger.info("Executing slow queue monitoring")

        siteIntegrationGateway.findSlow().forEach(puppeteerAPIGateway::execute)
    }

}