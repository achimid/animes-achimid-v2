package br.com.achimid.animesachimidv2.domains

import java.time.Instant

data class User(
    val id: String,
    val email: String? = null,
    val username: String? = null,
    val picture: String? = null,
    val googleId: String? = null,
    val favorites: Set<String>? = null,
    val isAdmin: Boolean = false,
    val createdAt: Instant? = null,
    val lastLoginAt: Instant? = null,
    val notificationSitePreferences: Map<String, Set<String>>? = null,
) {
    fun memberSince(): String? = createdAt?.toString()?.substring(0, 10)
    fun lastLoginDate(): String? = lastLoginAt?.toString()?.substring(0, 10)
}
