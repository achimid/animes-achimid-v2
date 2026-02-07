package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.dto.AnimeReleasesResponse
import br.com.achimid.animesachimidv2.usecases.MockUseCase
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/release")
class ReleaseAPIController(
    val mockUseCase: MockUseCase
) {

    @GetMapping
    @ResponseStatus(OK)
    fun releases(): AnimeReleasesResponse {
        return AnimeReleasesResponse(mockUseCase.getLastReleases())
    }

}