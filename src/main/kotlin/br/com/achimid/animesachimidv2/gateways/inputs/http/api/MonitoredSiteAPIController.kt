package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.MonitoredSites
import br.com.achimid.animesachimidv2.usecases.RetrieveMonitoredSitesUseCase
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/monitored/site")
class MonitoredSiteAPIController(
    val retrieveMonitoredSites: RetrieveMonitoredSitesUseCase
) {

    @GetMapping
    @ResponseStatus(OK)
    fun find(): MonitoredSites = MonitoredSites(retrieveMonitoredSites.execute())

}