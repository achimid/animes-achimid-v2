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
        val today = dateFormatter.format(java.time.Instant.now())

        val staticUrls = listOf(
            SitemapUrl("https://isekaihub.com.br/", today, "daily", "0.9"),
            SitemapUrl("https://isekaihub.com.br/animes", today, "daily", "0.9"),
            SitemapUrl("https://isekaihub.com.br/calendar", today, "daily", "0.8"),
        )

        val legalUrls = listOf("dmca", "cookies", "privacy", "terms").map { page ->
            SitemapUrl("https://isekaihub.com.br/$page", null, "yearly", "0.3")
        }

        val animeUrls = animeGateway.findSitemapData().map { entry ->
            SitemapUrl(
                loc = "https://isekaihub.com.br/anime/${entry.slug}",
                lastmod = entry.updatedAt?.let { dateFormatter.format(it) },
                changefreq = "weekly",
                priority = "0.6",
            )
        }

        model.addAttribute("urls", staticUrls + legalUrls + animeUrls)
        return "sitemap"
    }
}
