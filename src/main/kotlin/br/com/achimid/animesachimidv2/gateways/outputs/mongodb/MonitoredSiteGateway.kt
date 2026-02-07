package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.MonitoredSite
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers.MonitoredSiteDocumentMapper
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.MonitoredSitesRepository
import org.springframework.stereotype.Component

@Component
class MonitoredSiteGateway(
    val repository: MonitoredSitesRepository,
    val mapper: MonitoredSiteDocumentMapper,
) {

    fun findAll(): List<MonitoredSite> = repository.findAll().map(mapper::fromDocument)

}