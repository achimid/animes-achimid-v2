package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.domains.SiteIntegrationType
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.SiteIntegrationDocument
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SiteIntegrationMongoRepository : MongoRepository<SiteIntegrationDocument, String> {

    fun findByName(name: String): SiteIntegrationDocument?

    fun findByTypeAndEnabledTrue(type: SiteIntegrationType): List<SiteIntegrationDocument>
}
