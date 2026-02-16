package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.NameDocument
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.data.mongodb.core.query.TextQuery
import org.springframework.stereotype.Component

@Component
class NamesRepository(private val mongoTemplate: MongoTemplate) {

    fun searchByProximity(text: String, limit: Int = 20, caseSensitive: Boolean = false): List<NameDocument> {
        val criteria = TextCriteria.forDefaultLanguage()
            .matching(text)
            .caseSensitive(caseSensitive)

        val query = TextQuery.queryText(criteria).sortByScore().limit(limit)
        return mongoTemplate.find<NameDocument>(query)
    }

    fun save(name: NameDocument) = mongoTemplate.save(name)

}