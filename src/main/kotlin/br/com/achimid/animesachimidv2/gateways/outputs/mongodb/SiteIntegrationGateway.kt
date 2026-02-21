package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.SiteIntegration
import br.com.achimid.animesachimidv2.gateways.inputs.http.api.request.CallbackIntegrationExecutionResult
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.IntegrationEventDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.MirrorDataDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.MirrorDocument
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.SiteIntegrationRepository
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old.IntegrationEventRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class SiteIntegrationGateway(
    val siteIntegrationRepository: SiteIntegrationRepository,
    val integrationEventRepository: IntegrationEventRepository
) {

    val logger = LoggerFactory.getLogger(this.javaClass)

    fun findAll(): List<SiteIntegration> = siteIntegrationRepository.findAll()

    fun findSlow(): List<SiteIntegration> = siteIntegrationRepository.findSlow()

    fun findMedium(): List<SiteIntegration> = siteIntegrationRepository.findMedium()

    fun findFast(): List<SiteIntegration> = siteIntegrationRepository.findFast()

    fun updateByName(name: String, success: Boolean, withRelease: Boolean = false) {
        siteIntegrationRepository.findByName(name).let {
            it.lastExecutionSuccess = success
            it.lastExecutionDate = Instant.now()
            if (withRelease) it.lastExecutionDateWithReleaseSuccess = Instant.now()
        }
    }

    fun createEvenIntegration(result: CallbackIntegrationExecutionResult): Boolean {
        val idt = result.getIdt()

        val eventIntegration = integrationEventRepository.findByIdt(idt)
        if (eventIntegration != null) return false

        integrationEventRepository.save(IntegrationEventDocument(
            idt = idt,
            from = result.from,
            url = result.url,
            title = result.title,
            anime = result.anime,
            episode = result.episode,
            data = MirrorDataDocument(result.data?.mirrors?.map { MirrorDocument(it.description, it.url) }),
        ))

        logger.info("New event integration saved: $idt")

        return true
    }
}