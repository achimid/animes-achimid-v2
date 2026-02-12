package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.PageAccessDocument
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component

@Component
class PageAccessRepository(
    private val mongoTemplate: MongoTemplate
) {

    fun incrementCounter() : PageAccessDocument {
        return mongoTemplate.findAndModify(
            Query(Criteria.where("id").`is`(1)),
            Update().inc("totalCount", 1),
            FindAndModifyOptions.options().returnNew(true),
            PageAccessDocument::class.java
        ) ?: mongoTemplate.save(PageAccessDocument())
    }

    @Cacheable("pageAccessCache")
    fun getPageAccess(): PageAccessDocument = mongoTemplate.findById(1, PageAccessDocument::class.java) ?: PageAccessDocument()

}