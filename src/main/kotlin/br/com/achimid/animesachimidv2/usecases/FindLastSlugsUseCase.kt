package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class FindLastSlugsUseCase(
    private val animeGateway: AnimeGateway
) {

    fun execute() : List<String> = animeGateway.findLastSlugs()

}