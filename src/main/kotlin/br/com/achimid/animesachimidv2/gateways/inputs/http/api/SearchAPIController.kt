package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/search")
class SearchAPIController {

    @GetMapping()
    @ResponseStatus(OK)
    fun search(): Any? = null

}