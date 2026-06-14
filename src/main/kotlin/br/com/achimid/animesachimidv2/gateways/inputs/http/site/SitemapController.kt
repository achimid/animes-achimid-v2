package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.AnimeGateway
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class SitemapUrl(val loc: String, val lastmod: String?, val changefreq: String, val priority: String)

@Controller
class SitemapController(val animeGateway: AnimeGateway) {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC)

    @GetMapping(value = ["/sitemap.xml"], produces = ["application/xml"])
    fun sitemap(model: Model): String {
        val staticUrls = listOf(
            SitemapUrl("https://animes.achimid.com.br/", null, "daily", "0.9"),
            SitemapUrl("https://animes.achimid.com.br/animes", null, "daily", "0.9"),
            SitemapUrl("https://animes.achimid.com.br/calendar", null, "daily", "0.8"),
        )

        val animeUrls = animeGateway.findSitemapData().map { entry ->
            SitemapUrl(
                loc = "https://animes.achimid.com.br/anime/${entry.slug}",
                lastmod = entry.updatedAt?.let { dateFormatter.format(it) },
                changefreq = "weekly",
                priority = "0.6",
            )
        }

        model.addAttribute("urls", staticUrls + animeUrls)
        return "sitemap"
    }
}
