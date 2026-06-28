package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "calendar_snapshots")
data class CalendarSnapshotDocument(
    @Id val id: String = "full_calendar",
    val data: String,
    val savedAt: Instant = Instant.now(),
)
