package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.gateways.inputs.http.api.docs.HomeControllerDoc
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.mappers.AnimeDetailsDocumentMapper
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.AnimeDetailsRepository
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/home")
class HomeAPIController(
    val mapper: AnimeDetailsDocumentMapper,
    val repository: AnimeDetailsRepository
) : HomeControllerDoc {

    @GetMapping()
    @ResponseStatus(OK)
    override fun home(): String {

        val first = repository.findAll().first()
        mapper.fromDocument(first)

        return "Hello"
    }

}