package br.com.achimid.animesachimidv2.configurations

import br.com.achimid.animesachimidv2.usecases.FindUserUseCase
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

/**
 * Verificação de acesso administrativo (FUNC-04/FUNC-05). Mantém o modelo existente do projeto:
 * o usuário é identificado pelo cookie `user_id` e a flag `isAdmin` vem do seu documento.
 */
@Component
class AdminAccessChecker(
    private val findUserUseCase: FindUserUseCase
) {

    fun isAdmin(userId: String?): Boolean =
        userId?.let { findUserUseCase.execute(it)?.isAdmin } == true

    fun requireAdmin(userId: String?) {
        if (!isAdmin(userId)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito a administradores")
        }
    }
}
