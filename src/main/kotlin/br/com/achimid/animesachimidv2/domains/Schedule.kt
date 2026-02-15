package br.com.achimid.animesachimidv2.domains

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter.ofPattern

@JsonIgnoreProperties(ignoreUnknown = true)
data class Schedule(
    val tz: String,
    val schedule: Map<String, List<ScheduleItem>>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ScheduleItem(
    val title: String,
    val time: String,
    val released: Boolean = false,
    @JsonProperty("image_url")
    val image: String? = null,
    val imageUrl: String? = "https://subsplease.org$image",
) {
    fun shouldBeReleased(): Boolean {
        val zone = ZoneId.of("America/Sao_Paulo")
        val now = LocalTime.now(zone)
        val scheduleTime = LocalTime.parse(time, ofPattern("HH:mm"))
        return scheduleTime.isBefore(now)
    }
}

data class CalendarRelease (
    val releasesToday: List<ScheduleItem> = emptyList(),
)
