package br.com.achimid.animesachimidv2.domains.dto

data class CalendarRelease (
    val releasesToday: List<CalendarItemDTO> = emptyList(),
)

data class CalendarItemDTO(
    val name: String,
    val time: String,
    val released: Boolean = false
)