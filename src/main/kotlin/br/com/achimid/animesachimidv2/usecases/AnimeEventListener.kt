package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.AnimeCreatedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AnimeEventListener(private val translateAnimeInfoUserCase: TranslateAnimeInfoUserCase) {

    @Async
    @EventListener
    fun onAnimeCreated(event: AnimeCreatedEvent) = translateAnimeInfoUserCase.execute(event.anime)
}
