package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.util.Arrays


@Controller
class SitemapController {

    @GetMapping(value = ["/sitemap.xml"], produces = ["application/xml"])
    fun sitemap(model: Model): String {
        val dailyUrls: MutableList<String?>? = Arrays.asList("/", "/animes", "/sites")
        val weeklyUrls: MutableList<String?>? = Arrays.asList("/info", "/anime")

        model.addAttribute("dailyUrls", dailyUrls)
        model.addAttribute("weeklyUrls", weeklyUrls)

        return "sitemap"
    }
    
}