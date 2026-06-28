package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.CalendarSnapshotDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface CalendarSnapshotMongoRepository : MongoRepository<CalendarSnapshotDocument, String>
