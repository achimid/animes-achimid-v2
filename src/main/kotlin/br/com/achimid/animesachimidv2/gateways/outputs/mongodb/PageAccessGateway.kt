package br.com.achimid.animesachimidv2.gateways.outputs.mongodb

import br.com.achimid.animesachimidv2.domains.PageAccess
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers.PageAccessDocumentMapper
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.PageAccessRepository
import org.springframework.stereotype.Component

@Component
class PageAccessGateway(
    val repository: PageAccessRepository,
    val mapper: PageAccessDocumentMapper,
) {

    fun incrementCounter(): PageAccess = repository.incrementCounter().let(mapper::fromDocument)

    fun getPageAccess(): PageAccess = repository.getPageAccess().let(mapper::fromDocument)

}