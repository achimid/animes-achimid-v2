package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Release
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.stereotype.Component

@Component
class FindReleasesUseCase(
    private val releaseGateway: ReleaseGateway,
) {

    fun execute(pageNumber: Int, pageSize: Int, query: String?= null) : Page<Release> {
        val pageRequest = PageRequest.of(pageNumber, pageSize, DESC, "updatedAt")

        if (query.isNullOrEmpty()) return releaseGateway.findAll(pageRequest)

        return releaseGateway.findByTitle(pageRequest, query)
    }

}