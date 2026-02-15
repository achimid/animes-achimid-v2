package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.SiteIntegration
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.SiteIntegrationGateway
import org.springframework.stereotype.Component

@Component
class FindSiteIntegrationsUseCase(
    private val siteIntegrationGateway: SiteIntegrationGateway
) {

    fun execute(query: String? = null): List<SiteIntegration> {

        if (query.isNullOrEmpty()) return siteIntegrationGateway.findAll()

        return siteIntegrationGateway.findAll().filter { it.name.contains(query, ignoreCase = true) }
    }

}