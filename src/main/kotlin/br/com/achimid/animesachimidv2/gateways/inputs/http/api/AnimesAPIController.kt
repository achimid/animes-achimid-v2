package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.usecases.FindAnimesUseCase
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/animes")
class AnimesAPIController(
    val findAnimesUseCase: FindAnimesUseCase
) {

    @GetMapping
    @ResponseStatus(OK)
    fun releases(
        @RequestParam(required = false) pageNumber: Int = 0,
        @RequestParam(required = false) pageSize: Int = 12,
        @RequestParam(required = false) query: String? = null,
    ): Page<Anime> {
        return findAnimesUseCase.execute(pageNumber, pageSize, query)
    }

}