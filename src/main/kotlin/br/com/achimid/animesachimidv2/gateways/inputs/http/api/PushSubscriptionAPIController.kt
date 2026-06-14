package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.PushSubscriptionGateway
import br.com.achimid.animesachimidv2.usecases.SavePushSubscriptionUseCase
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*

data class PushSubscribeRequest(
    val endpoint: String,
    val p256dh: String,
    val auth: String,
)

@RestController
@RequestMapping("/api/v1/user/push")
class PushSubscriptionAPIController(
    val savePushSubscriptionUseCase: SavePushSubscriptionUseCase,
    val pushSubscriptionGateway: PushSubscriptionGateway,
) {

    @PostMapping("/subscribe")
    @ResponseStatus(NO_CONTENT)
    fun subscribe(
        @CookieValue(value = "user_id", required = false) userId: String?,
        @RequestBody request: PushSubscribeRequest,
    ) {
        if (userId.isNullOrEmpty()) return
        savePushSubscriptionUseCase.execute(userId, request.endpoint, request.p256dh, request.auth)
    }

    @DeleteMapping("/subscribe")
    @ResponseStatus(NO_CONTENT)
    fun unsubscribe(
        @CookieValue(value = "user_id", required = false) userId: String?,
        @RequestBody request: PushSubscribeRequest,
    ) {
        if (userId.isNullOrEmpty()) return
        pushSubscriptionGateway.deleteByEndpoint(request.endpoint)
    }
}
