package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.MonitoredSite
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.MonitoredSiteGateway
import org.springframework.stereotype.Component

@Component
class RetrieveMonitoredSitesUseCase(
    private val monitoredSiteGateway: MonitoredSiteGateway
) {

    fun execute() : List<MonitoredSite> = monitoredSiteGateway.findAll()

}