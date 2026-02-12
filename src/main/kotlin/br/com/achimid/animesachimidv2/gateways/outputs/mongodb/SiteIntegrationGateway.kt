package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.SiteIntegration
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers.SiteIntegrationDocumentMapper
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.SiteIntegrationRepository
import org.springframework.stereotype.Component

@Component
class SiteIntegrationGateway(
    val repository: SiteIntegrationRepository,
    val mapper: SiteIntegrationDocumentMapper,
) {

    fun findAll(): List<SiteIntegration> = repository.findAll().map(mapper::fromDocument)

    fun findSlow(): List<SiteIntegration> = repository.findSlow().map(mapper::fromDocument)

    fun findMedium(): List<SiteIntegration> = repository.findMedium().map(mapper::fromDocument)

    fun findFast(): List<SiteIntegration> = repository.findFast().map(mapper::fromDocument)

}