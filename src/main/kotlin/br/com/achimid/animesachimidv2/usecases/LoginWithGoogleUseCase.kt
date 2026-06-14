package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.User
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Trata o login via Google (F1). Resolve a conta do usuário a partir do e-mail e,
 * quando há uma sessão anônima (cookie `user_id`), **mescla** os favoritos do convidado
 * na conta autenticada — preservando dados ao "promover" um convidado a conta Google.
 *
 * Retorna o [User] da conta; o id resultante deve ser usado para repontar o cookie `user_id`,
 * de modo que os fluxos existentes (favoritar/comentar por cookie) passem a operar na conta.
 */
@Component
class LoginWithGoogleUseCase(
    private val userGateway: UserGateway
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(
        email: String,
        name: String?,
        picture: String?,
        googleId: String?,
        guestUserId: String?
    ): User {
        val guest = guestUserId?.let { userGateway.findById(it) }?.takeIf { it.email == null }
        val existing = userGateway.findByEmail(email)

        return when {
            // Conta já existe: mescla favoritos do convidado atual e atualiza o perfil.
            existing != null -> {
                val mergedFavorites = (existing.favorites ?: emptySet()) + (guest?.favorites ?: emptySet())
                userGateway.save(
                    existing.copy(
                        username = name ?: existing.username,
                        picture = picture ?: existing.picture,
                        googleId = googleId ?: existing.googleId,
                        favorites = mergedFavorites
                    )
                ).also { logger.info("Login Google: conta existente ${it.id} (mesclados ${guest?.favorites?.size ?: 0} favoritos)") }
            }

            // Promove o convidado a conta (mantém o mesmo id → cookie inalterado e favoritos preservados).
            guest != null -> userGateway.save(
                guest.copy(email = email, username = name ?: guest.username, picture = picture, googleId = googleId)
            ).also { logger.info("Login Google: convidado ${it.id} promovido a conta") }

            // Sem convidado e sem conta: cria do zero.
            else -> userGateway.save(
                User(id = UUID.randomUUID().toString(), email = email, username = name, picture = picture, googleId = googleId)
            ).also { logger.info("Login Google: nova conta ${it.id}") }
        }
    }
}
