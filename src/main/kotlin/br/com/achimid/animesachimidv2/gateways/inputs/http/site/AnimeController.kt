package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.usecases.FindAnimeUseCase
import br.com.achimid.animesachimidv2.usecases.FindNextEpisodeUseCase
import br.com.achimid.animesachimidv2.usecases.FindRecommendationsUseCase
import br.com.achimid.animesachimidv2.usecases.FindReleasesUseCase
import br.com.achimid.animesachimidv2.usecases.RegisterAnimeVisitUseCase
import br.com.achimid.animesachimidv2.usecases.FindUserUseCase
import org.bson.types.ObjectId
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.concurrent.CompletableFuture.allOf
import java.util.concurrent.CompletableFuture.supplyAsync

private const val BASE_URL = "https://animes.achimid.com.br"

/** Escapa caracteres especiais para uso seguro em JSON. */
private fun String.jsonEscape() = this
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("\n", "\\n")
    .replace("\r", "\\r")
    .replace("\t", "\\t")

@Controller
@RequestMapping("/anime")
class AnimeController(
    val findAnimeUseCase: FindAnimeUseCase,
    val findReleasesUseCase: FindReleasesUseCase,
    val registerAnimeVisitUseCase: RegisterAnimeVisitUseCase,
    val findRecommendationsUseCase: FindRecommendationsUseCase,
    val findUserUseCase: FindUserUseCase,
    val findNextEpisodeUseCase: FindNextEpisodeUseCase,
) {

    @GetMapping("/{idOrSlug}")
    fun animePage(
        model: Model,
        @PathVariable idOrSlug: String,
        @CookieValue(value = "user_id", required = false) userId: String? = null
    ): String {
        if (ObjectId.isValid(idOrSlug)) return "redirect:/"

        val animeSupply = supplyAsync { findAnimeUseCase.execute(idOrSlug) }
        val releasesSupply = supplyAsync { findReleasesUseCase.execute(0, 5) }
        val userSupply = supplyAsync { if (userId != null) findUserUseCase.execute(userId) else null }

        allOf(animeSupply, releasesSupply, userSupply).join()

        val anime = animeSupply.join()
        val user = userSupply.join()

        val recommendationsSupply = supplyAsync { findRecommendationsUseCase.forAnime(anime, 3) }
        val nextEpisodeSupply = supplyAsync { findNextEpisodeUseCase.execute(anime.id) }
        allOf(recommendationsSupply, nextEpisodeSupply).join()

        model.addAttribute("anime", anime)
        model.addAttribute("releases", releasesSupply.join())
        model.addAttribute("recommendations", recommendationsSupply.join())
        model.addAttribute("nextEpisode", nextEpisodeSupply.join())
        model.addAttribute("isAdmin", user?.isAdmin ?: false)
        model.addAttribute("isFavorited", user?.favorites?.contains(anime.id) ?: false)
        model.addAttribute("metaDescription", buildMetaDescription(anime))
        model.addAttribute("jsonLdTvSeries", buildTvSeriesJsonLd(anime))
        model.addAttribute("jsonLdBreadcrumb", buildBreadcrumbJsonLd(anime))

        registerAnimeVisitUseCase.execute(anime.id)

        return "anime"
    }

    private fun buildMetaDescription(anime: Anime): String {
        val genres = anime.tags?.take(3)?.joinToString(", ") ?: ""
        val scoreStr = anime.score?.let { " · Score ${it}" } ?: ""
        val genreStr = if (genres.isNotEmpty()) " · $genres" else ""
        val synopsis = anime.getDescriptionTranslated()?.take(120)?.trimEnd()
            ?.let { if (it.length == 120) "$it…" else it } ?: ""
        return "${anime.name}$scoreStr$genreStr. $synopsis".trim().trimEnd('.')
    }

    private fun buildTvSeriesJsonLd(anime: Anime): String {
        val parts = mutableListOf(
            """"@context":"https://schema.org"""",
            """"@type":"TVSeries"""",
            """"name":"${anime.name.jsonEscape()}"""",
            """"url":"$BASE_URL/anime/${anime.slug}"""",
        )
        if (!anime.imageUrl.isNullOrBlank()) parts += """"image":"${anime.imageUrl.jsonEscape()}""""
        if (!anime.nameSecondary.isNullOrBlank()) parts += """"alternateName":"${anime.nameSecondary!!.jsonEscape()}""""
        val description = anime.getDescriptionTranslated()
        if (!description.isNullOrBlank()) parts += """"description":"${description.jsonEscape()}""""
        val genres = anime.tags
        if (!genres.isNullOrEmpty()) parts += """"genre":[${genres.joinToString(",") { "\"${it.jsonEscape()}\"" }}]"""
        val epCount = anime.episodes?.size?.takeIf { it > 0 }
        if (epCount != null) parts += """"numberOfEpisodes":$epCount"""
        if (anime.score != null) parts += """"aggregateRating":{"@type":"AggregateRating","ratingValue":${anime.score},"bestRating":10,"worstRating":1}"""
        return "{${parts.joinToString(",")}}"
    }

    private fun buildBreadcrumbJsonLd(anime: Anime): String = """
        {"@context":"https://schema.org","@type":"BreadcrumbList","itemListElement":[
          {"@type":"ListItem","position":1,"name":"Home","item":"$BASE_URL/"},
          {"@type":"ListItem","position":2,"name":"Animes","item":"$BASE_URL/animes"},
          {"@type":"ListItem","position":3,"name":"${anime.name.jsonEscape()}"}
        ]}""".trimIndent()
}