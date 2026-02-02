package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface AnimeDetailsDocumentMapper {

//    fun fromDocument(document: AnimeDetailsDocument): AnimeDetails
//    fun toDocument(domain: AnimeDetails): AnimeDetailsDocument

}
