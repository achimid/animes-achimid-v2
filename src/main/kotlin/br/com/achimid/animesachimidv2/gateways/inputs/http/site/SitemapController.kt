package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.usecases.FindLastSlugsUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.*


@Controller
class SitemapController(
    val findLastSlugsUseCase: FindLastSlugsUseCase
) {

    @GetMapping(value = ["/sitemap.xml"], produces = ["application/xml"])
    fun sitemap(model: Model): String {
        val dailyUrls: MutableList<String?>? = Arrays.asList("/", "/animes", "/sites")
        val weeklyUrls: List<String> = findLastSlugsUseCase.execute().map { "/anime/$it" }

        model.addAttribute("dailyUrls", dailyUrls)
        model.addAttribute("weeklyUrls", weeklyUrls)

        return "sitemap"
    }
    
}