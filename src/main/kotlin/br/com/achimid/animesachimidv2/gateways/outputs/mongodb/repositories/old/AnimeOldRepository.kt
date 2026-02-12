package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.old.AnimeOldDocument
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AnimeOldRepository: MongoRepository<AnimeOldDocument, ObjectId>