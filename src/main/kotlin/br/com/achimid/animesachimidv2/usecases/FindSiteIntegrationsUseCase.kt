package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.SiteIntegration
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.SiteIntegrationGateway
import org.springframework.stereotype.Component

@Component
class FindSiteIntegrationsUseCase(
    private val siteIntegrationGateway: SiteIntegrationGateway
) {

    fun execute() : List<SiteIntegration> = siteIntegrationGateway.findAll()

}