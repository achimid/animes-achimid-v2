package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.IntegrationEventDocument
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface IntegrationEventRepository: CrudRepository<IntegrationEventDocument, String> {

    fun findByIdt(idt: String): IntegrationEventDocument?

}