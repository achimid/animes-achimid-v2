package br.com.achimid.animesachimidv2.domains

data class PushSubscription(
    val id: String? = null,
    val userId: String,
    val endpoint: String,
    val p256dh: String,
    val auth: String,
)
