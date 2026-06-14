package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

/**
 * Páginas de informações legais (FUNC-11). Todas renderizam o mesmo template `legal.html`,
 * que seleciona o conteúdo pelo atributo `page`.
 */
@Controller
class LegalController {

    @GetMapping("/dmca")
    fun dmca(model: Model): String = render(
        model, "dmca", "DMCA e Direitos Autorais",
        "Política de direitos autorais do Animes Achimid. Entenda como funcionamos e como solicitar remoção de conteúdo indexado."
    )

    @GetMapping("/cookies")
    fun cookies(model: Model): String = render(
        model, "cookies", "Política de Cookies",
        "Saiba quais cookies o Animes Achimid utiliza, para que servem e como você pode gerenciá-los ou recusá-los."
    )

    @GetMapping("/privacidade")
    fun privacy(model: Model): String = render(
        model, "privacidade", "Política de Privacidade",
        "Política de privacidade do Animes Achimid conforme a LGPD. Saiba quais dados coletamos e como os protegemos."
    )

    @GetMapping("/termos")
    fun terms(model: Model): String = render(
        model, "termos", "Termos de Uso",
        "Termos e condições de uso do Animes Achimid. Conheça as regras para utilizar o site."
    )

    private fun render(model: Model, page: String, title: String, description: String): String {
        model.addAttribute("page", page)
        model.addAttribute("pageTitle", title)
        model.addAttribute("pageDescription", description)
        return "legal"
    }
}
