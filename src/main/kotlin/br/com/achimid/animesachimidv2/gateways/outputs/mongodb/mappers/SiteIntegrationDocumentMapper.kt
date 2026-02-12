package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers

import br.com.achimid.animesachimidv2.domains.SiteIntegration
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.SiteIntegrationDocument
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants.ComponentModel.SPRING

@Mapper(componentModel = SPRING)
interface SiteIntegrationDocumentMapper {

    fun fromDocument(document: SiteIntegrationDocument): SiteIntegration

}
