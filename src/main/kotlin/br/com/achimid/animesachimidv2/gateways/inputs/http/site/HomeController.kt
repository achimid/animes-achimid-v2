package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/")
class HomeController {

    @GetMapping
    fun homePage(): String = "home"

    @GetMapping("/500")
    fun triggerError(): String = throw RuntimeException("Testing 500 exception")

}