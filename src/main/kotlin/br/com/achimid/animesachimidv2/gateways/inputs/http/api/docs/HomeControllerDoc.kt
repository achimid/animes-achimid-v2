package br.com.achimid.animesachimidv2.gateways.inputs.http.api.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Home", description = "Home operations")
interface HomeControllerDoc {

    @Operation(summary = "Operations from home page")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Example"),
            ApiResponse(responseCode = "400", description = "Example"),
            ApiResponse(responseCode = "404", description = "Example")
        ]
    )
    fun home(): String

}