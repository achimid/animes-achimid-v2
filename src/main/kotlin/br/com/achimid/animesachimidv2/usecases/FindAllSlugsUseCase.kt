package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.springframework.stereotype.Component

@Component
class FindAllSlugsUseCase(
    private val animeGateway: AnimeGateway
) {

    fun execute() : List<String> = animeGateway.findAllSlugs()

}