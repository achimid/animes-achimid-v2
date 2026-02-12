package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers

import br.com.achimid.animesachimidv2.domains.PageAccess
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.PageAccessDocument
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants.ComponentModel.SPRING

@Mapper(componentModel = SPRING)
interface PageAccessDocumentMapper {

    fun fromDocument(document: PageAccessDocument): PageAccess = PageAccess(document.totalCount)

}
