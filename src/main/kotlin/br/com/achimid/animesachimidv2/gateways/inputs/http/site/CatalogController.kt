package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.MockUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/catalog")
class CatalogController(
    val mockUseCase: MockUseCase
) {

    @GetMapping
    fun catalogPage(model: Model): String {

        val catalogList = mockUseCase.getCatalogList()

        model.addAttribute("catalogList", catalogList)

        return "catalog"
    }

}