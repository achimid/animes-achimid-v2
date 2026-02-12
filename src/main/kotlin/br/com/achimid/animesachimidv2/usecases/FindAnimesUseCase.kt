package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.PageRequest.of
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.stereotype.Component

@Component
class FindAnimesUseCase(
    private val animeGateway: AnimeGateway
) {

    fun execute(pageNumber: Int, pageSize: Int, query: String?= null) : Page<Anime> {

        if (query.isNullOrEmpty()) {
            return animeGateway.findAll(of(pageNumber, pageSize, DESC, "updatedAt"))
        }

        return animeGateway.findByName(of(pageNumber, pageSize, ASC, "name"), query)
    }

}