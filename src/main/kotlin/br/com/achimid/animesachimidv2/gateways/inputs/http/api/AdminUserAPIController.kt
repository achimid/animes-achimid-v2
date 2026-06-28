package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.configurations.AdminAccessChecker
import br.com.achimid.animesachimidv2.domains.User
import br.com.achimid.animesachimidv2.usecases.FindGoogleUsersUseCase
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/users")
class AdminUserAPIController(
    val findGoogleUsersUseCase: FindGoogleUsersUseCase,
    val adminAccessChecker: AdminAccessChecker,
) {

    @GetMapping
    @ResponseStatus(OK)
    fun listUsers(
        @RequestParam(required = false) page: Int = 0,
        @RequestParam(required = false) size: Int = 20,
        @RequestParam(required = false) query: String? = null,
        @CookieValue(value = "user_id", required = false) userId: String? = null,
    ): Page<User> {
        adminAccessChecker.requireAdmin(userId)
        return findGoogleUsersUseCase.execute(page, size, query)
    }
}
