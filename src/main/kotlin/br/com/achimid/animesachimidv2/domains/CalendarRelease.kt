package br.com.achimid.animesachimidv2.domains

data class CalendarRelease (
    val releasesToday: List<CalendarItem> = emptyList(),
)

data class CalendarItem(
    val name: String,
    val time: String,
    val released: Boolean = false
)