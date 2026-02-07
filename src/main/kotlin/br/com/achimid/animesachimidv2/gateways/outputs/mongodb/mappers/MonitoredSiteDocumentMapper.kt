package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers

import br.com.achimid.animesachimidv2.domains.MonitoredSite
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.MonitoredSiteDocument
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants.ComponentModel.SPRING

@Mapper(componentModel = SPRING)
interface MonitoredSiteDocumentMapper {

    fun fromDocument(document: MonitoredSiteDocument): MonitoredSite

}
