package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.usecases.FindFavoriteAnimesUseCase
import br.com.achimid.animesachimidv2.usecases.FindUserUseCase
import br.com.achimid.animesachimidv2.usecases.UpdateNotificationSitePreferenceUseCase
import br.com.achimid.animesachimidv2.usecases.UpdateUserSettingsUseCase
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class UserSettingsRequest(val username: String? = null)
data class NotificationSiteRequest(val sites: Set<String>? = null)
data class TransferUserRequest(val userId: String)

private val UUID_REGEX = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")

@RestController
@RequestMapping("/api/v1/user")
class UserAPIController(
    val findFavoriteAnimesUseCase: FindFavoriteAnimesUseCase,
    val findUserUseCase: FindUserUseCase,
    val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    val updateNotificationSitePreferenceUseCase: UpdateNotificationSitePreferenceUseCase,
) {

    @GetMapping("/favorites")
    @ResponseStatus(OK)
    fun favorites(
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ): List<Anime> = findFavoriteAnimesUseCase.execute(userId)

    @PatchMapping("/settings")
    fun updateSettings(
        @CookieValue(value = "user_id", required = false) userId: String?,
        @RequestBody body: UserSettingsRequest,
    ): ResponseEntity<Void> {
        if (userId.isNullOrEmpty()) return ResponseEntity.status(401).build()
        val updated = updateUserSettingsUseCase.execute(userId, body.username)
        return if (updated) ResponseEntity.noContent().build() else ResponseEntity.notFound().build()
    }

    @PutMapping("/notification-preference/{animeId}")
    fun setNotificationSitePreference(
        @CookieValue(value = "user_id", required = false) userId: String?,
        @PathVariable animeId: String,
        @RequestBody body: NotificationSiteRequest,
    ): ResponseEntity<Void> {
        if (userId.isNullOrEmpty()) return ResponseEntity.status(401).build()
        val updated = updateNotificationSitePreferenceUseCase.execute(userId, animeId, body.sites)
        return if (updated) ResponseEntity.noContent().build() else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/notification-preference/{animeId}")
    fun deleteNotificationSitePreference(
        @CookieValue(value = "user_id", required = false) userId: String?,
        @PathVariable animeId: String,
    ): ResponseEntity<Void> {
        if (userId.isNullOrEmpty()) return ResponseEntity.status(401).build()
        updateNotificationSitePreferenceUseCase.execute(userId, animeId, null)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/transfer")
    fun transferUserId(
        @RequestBody body: TransferUserRequest,
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        if (!body.userId.matches(UUID_REGEX)) return ResponseEntity.badRequest().build()
        findUserUseCase.execute(body.userId) ?: return ResponseEntity.notFound().build()
        val cookie = Cookie("user_id", body.userId).apply {
            path = "/"
            maxAge = 60 * 60 * 24 * 365
        }
        response.addCookie(cookie)
        return ResponseEntity.noContent().build()
    }
}
