package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers

import br.com.achimid.animesachimidv2.domains.User
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.UserDocument
import org.springframework.stereotype.Component

/**
 * Mapper manual (não MapStruct) para User ↔ UserDocument.
 * O MapStruct não mapeava de forma confiável o boolean `isAdmin` (quirk do prefixo `is` em Kotlin),
 * o que deixava a detecção de admin sempre `false`. Mapeamento explícito resolve (FUNC-05/FUNC-04).
 */
@Component
class UserDocumentMapper {

    fun fromDocument(document: UserDocument): User = User(
        id = document.id,
        email = document.email,
        username = document.username,
        picture = document.picture,
        googleId = document.googleId,
        favorites = document.favorites,
        isAdmin = document.isAdmin,
    )

    fun toDocument(domain: User): UserDocument = UserDocument(
        id = domain.id,
        email = domain.email,
        username = domain.username,
        picture = domain.picture,
        googleId = domain.googleId,
        favorites = domain.favorites,
        isAdmin = domain.isAdmin,
    )
}
