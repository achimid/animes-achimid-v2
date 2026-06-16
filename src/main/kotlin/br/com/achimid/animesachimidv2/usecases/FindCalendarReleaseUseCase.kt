package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Calendar
import br.com.achimid.animesachimidv2.domains.CalendarItem
import br.com.achimid.animesachimidv2.gateways.outputs.http.SubsPleaseAPIGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class FindCalendarReleaseUseCase(
    private val searchUseCase: SearchUseCase,
    private val subsPleaseAPIGateway: SubsPleaseAPIGateway,
    private val animeGateway: AnimeGateway,
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    private val weekDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    /**
     * Agenda híbrida (FUNC-08): combina a agenda do SubsPlease com os animes em exibição já ingeridos
     * (via broadcast do Jikan), unificados por id do anime. Se o SubsPlease falhar, monta uma semana
     * vazia e preenche só com os dados locais — então a agenda nunca fica indisponível (corrige o 500).
     */
    @EventListener(ApplicationReadyEvent::class, condition = "'\${spring.profiles.active}' == 'prod'")
    @Cacheable("calendarCache")
    fun execute(): Calendar {
        val calendar = try {
            subsPleaseAPIGateway.findFullSchedule().also { schedule ->
                val futures = schedule.schedule.entries.flatMapIndexed { index, entry ->
                    entry.value.map { item ->
                        item.dayIndex = index
                        CompletableFuture.supplyAsync {
                            item.anime = runCatching { searchUseCase.execute(item.title).anime }.getOrNull()
                        }
                    }
                }
                CompletableFuture.allOf(*futures.toTypedArray()).join()
            }
        } catch (ex: RuntimeException) {
            logger.error("Erro ao buscar a agenda do SubsPlease; usando apenas dados locais", ex)
            Calendar(tz = "America/Sao_Paulo", schedule = weekDays.associateWith { mutableListOf() })
        }

        try {
            mergeLocalAiring(calendar)
        } catch (ex: Exception) {
            logger.warn("Falha ao mesclar animes locais na agenda (FUNC-08)", ex)
        }

        return calendar
    }

    private fun mergeLocalAiring(calendar: Calendar) {
        val dayKeys = calendar.schedule.keys.toList()
        if (dayKeys.isEmpty()) return

        val existingIds = calendar.schedule.values.flatten().mapNotNull { it.anime?.id }.toMutableSet()

        animeGateway.findAiringScheduled().forEach { sched ->
            val animeId = sched.anime.id
            if (animeId in existingIds) return@forEach

            val key = matchDayKey(dayKeys, sched.day) ?: return@forEach
            val item = CalendarItem(
                title = sched.anime.name,
                time = sched.time?.takeIf { it.matches(Regex("\\d{2}:\\d{2}")) } ?: "--:--",
                imageUrl = sched.anime.imageUrl,
                anime = sched.anime,
                dayIndex = dayKeys.indexOf(key),
            )

            @Suppress("UNCHECKED_CAST")
            (calendar.schedule[key] as MutableList<CalendarItem>).add(item)
            existingIds.add(animeId)
        }
    }

    /** Casa o dia do Jikan ("Mondays") com a chave da agenda ("Monday"). */
    private fun matchDayKey(dayKeys: List<String>, jikanDay: String?): String? {
        if (jikanDay == null) return null
        val norm = jikanDay.lowercase().removeSuffix("s")
        return dayKeys.firstOrNull { it.lowercase().removeSuffix("s") == norm }
    }
}
