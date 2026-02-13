package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.Release
import br.com.achimid.animesachimidv2.usecases.FindReleasesUseCase
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/release")
class ReleaseAPIController(
    val findReleasesUseCase: FindReleasesUseCase
) {

    @GetMapping
    @ResponseStatus(OK)
    fun releases(
        @RequestParam(required = false) pageNumber: Int = 0,
        @RequestParam(required = false) pageSize: Int = 10,
        @RequestParam(required = false) query: String? = null,
    ): Page<Release> {
        return findReleasesUseCase.execute(pageNumber, pageSize, query)
    }

}