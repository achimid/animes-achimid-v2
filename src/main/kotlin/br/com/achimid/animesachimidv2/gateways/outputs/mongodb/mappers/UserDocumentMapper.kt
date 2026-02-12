package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers

import br.com.achimid.animesachimidv2.domains.User
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.UserDocument
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants.ComponentModel.SPRING

@Mapper(componentModel = SPRING)
interface UserDocumentMapper {

    fun fromDocument(document: UserDocument): User

    fun toDocument(domain: User): UserDocument

}
