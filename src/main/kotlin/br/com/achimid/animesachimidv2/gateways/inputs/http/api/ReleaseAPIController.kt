package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.configurations.AdminAccessChecker
import br.com.achimid.animesachimidv2.domains.Release
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.ReleaseGateway
import br.com.achimid.animesachimidv2.usecases.FindReleasesUseCase
import br.com.achimid.animesachimidv2.usecases.HideReleaseUseCase
import br.com.achimid.animesachimidv2.usecases.ReassignReleaseUseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/release")
class ReleaseAPIController(
    val findReleasesUseCase: FindReleasesUseCase,
    val hideReleaseUseCase: HideReleaseUseCase,
    val reassignReleaseUseCase: ReassignReleaseUseCase,
    val releaseGateway: ReleaseGateway,
    val adminAccessChecker: AdminAccessChecker,
) {

    @GetMapping
    @ResponseStatus(OK)
    fun releases(
        @RequestParam(required = false) pageNumber: Int = 0,
        @RequestParam(required = false) pageSize: Int = 20,
        @RequestParam(required = false) query: String? = null,
    ): Page<Release> {
        return findReleasesUseCase.execute(pageNumber, pageSize, query)
    }

    @PostMapping("/{id}/hide")
    @ResponseStatus(NO_CONTENT)
    fun hide(
        @PathVariable id: String,
        @CookieValue(value = "user_id", required = false) userId: String? = null,
    ) {
        adminAccessChecker.requireAdmin(userId)
        hideReleaseUseCase.execute(id)
    }

    @PostMapping("/{id}/reassign")
    @ResponseStatus(NO_CONTENT)
    fun reassign(
        @PathVariable id: String,
        @RequestBody body: Map<String, String>,
        @CookieValue(value = "user_id", required = false) userId: String? = null,
    ) {
        adminAccessChecker.requireAdmin(userId)
        val animeSlug = requireNotNull(body["animeSlug"]) { "animeSlug obrigatório" }
        reassignReleaseUseCase.execute(id, animeSlug)
    }

    @GetMapping("/review")
    @ResponseStatus(OK)
    fun review(
        @RequestParam(required = false) pageNumber: Int = 0,
        @RequestParam(required = false) pageSize: Int = 30,
        @CookieValue(value = "user_id", required = false) userId: String? = null,
    ): Page<Release> {
        adminAccessChecker.requireAdmin(userId)
        return releaseGateway.findNeedingReview(PageRequest.of(pageNumber, pageSize))
    }
}
