package br.com.achimid.animesachimidv2.domains

data class User(
    val id: String,
    val email: String? = null,
    val username: String? = null,
    val picture: String? = null,
    val googleId: String? = null,
    val favorites: Set<String>? = null,
    val isAdmin: Boolean = false
)
