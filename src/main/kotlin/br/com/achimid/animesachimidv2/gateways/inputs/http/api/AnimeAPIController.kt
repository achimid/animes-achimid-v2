package br.com.achimid.animesachimidv2.gateways.inputs.http.api

import br.com.achimid.animesachimidv2.configurations.AdminAccessChecker
import br.com.achimid.animesachimidv2.domains.AnimeComment
import br.com.achimid.animesachimidv2.gateways.outputs.http.jikan.JikanAPIClient
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.UserGateway
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.NamesRepository
import br.com.achimid.animesachimidv2.usecases.AddCommentUserCase
import br.com.achimid.animesachimidv2.usecases.AddFavoriteUserCase
import br.com.achimid.animesachimidv2.usecases.FindAnimeUseCase
import br.com.achimid.animesachimidv2.usecases.RefreshAnimeUseCase
import br.com.achimid.animesachimidv2.usecases.RemoveFavoriteUserCase
import br.com.achimid.animesachimidv2.usecases.TranslateAnimeInfoUserCase
import br.com.achimid.animesachimidv2.usecases.UpdateAnimeImageUseCase
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/api/v1/anime")
class AnimeAPIController(
    val addCommentUserCase: AddCommentUserCase,
    val addFavoriteUserCase: AddFavoriteUserCase,
    val removeFavoriteUserCase: RemoveFavoriteUserCase,
    val userGateway: UserGateway,
    val findAnimeUseCase: FindAnimeUseCase,
    val jikanAPIClient: JikanAPIClient,
    val updateAnimeImageUseCase: UpdateAnimeImageUseCase,
    val refreshAnimeUseCase: RefreshAnimeUseCase,
    val translateAnimeInfoUserCase: TranslateAnimeInfoUserCase,
    val namesRepository: NamesRepository,
    val adminAccessChecker: AdminAccessChecker,
) {

    @ResponseStatus(CREATED)
    @PostMapping("/{id}/comment")
    fun addComment(
        @PathVariable id: String,
        @CookieValue(value = "user_id", required = false) userId: String?,
        @RequestBody comment: AnimeComment
    ): AnimeComment {
        val length = comment.content.trim().length
        if (length !in 3..1000) {
            throw ResponseStatusException(BAD_REQUEST, "O comentário deve ter entre 3 e 1000 caracteres")
        }
        val user = userId?.let { userGateway.findById(it) }
        val resolvedName = user?.username ?: user?.email?.substringBefore("@") ?: comment.userName
        val enriched = comment.copy(
            userId = userId ?: comment.userId,
            userName = resolvedName,
            avatar = resolvedName?.take(2)?.uppercase() ?: "AA",
        )
        return addCommentUserCase.execute(id, enriched)
    }

    @ResponseStatus(CREATED)
    @PostMapping("/{id}/favorite")
    fun addFavorite(
        @PathVariable id: String,
        @CookieValue(value = "user_id") userId: String
    ) {
        return addFavoriteUserCase.execute(id, userId)
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{id}/favorite")
    fun removeFavorite(
        @PathVariable id: String,
        @CookieValue(value = "user_id") userId: String
    ) {
        return removeFavoriteUserCase.execute(id, userId)
    }

    @GetMapping("/{slug}/images")
    @ResponseStatus(OK)
    fun images(@PathVariable slug: String): List<Map<String, String>> {
        val anime = findAnimeUseCase.execute(slug)
        val jikan = runCatching { jikanAPIClient.findById(anime.id).data }.getOrNull()
        val pictures = runCatching { jikanAPIClient.findPictures(anime.id).data }.getOrDefault(emptyList())

        val variants = listOfNotNull(
            jikan?.images?.jpg?.largeImageUrl?.let { mapOf("url" to it, "label" to "JPG Grande") },
            jikan?.images?.jpg?.imageUrl?.let { mapOf("url" to it, "label" to "JPG Padrão") },
            jikan?.images?.webp?.largeImageUrl?.let { mapOf("url" to it, "label" to "WebP Grande") },
            jikan?.images?.webp?.imageUrl?.let { mapOf("url" to it, "label" to "WebP Padrão") },
        )
        val variantUrls = variants.mapNotNull { it["url"] }.toSet()
        val alternatives = pictures
            .mapNotNull { it.jpg?.largeImageUrl ?: it.jpg?.imageUrl }
            .filter { it !in variantUrls }
            .mapIndexed { i, url -> mapOf("url" to url, "label" to "Alternativa ${i + 1}") }

        return variants + alternatives
    }

    @PatchMapping("/{slug}/image")
    @ResponseStatus(NO_CONTENT)
    fun updateImage(
        @PathVariable slug: String,
        @RequestBody body: Map<String, String>,
        @CookieValue(value = "user_id", required = false) userId: String? = null,
    ) {
        adminAccessChecker.requireAdmin(userId)
        val imageUrl = body["imageUrl"]?.trim()
        if (imageUrl.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "imageUrl obrigatório")
        if (!updateAnimeImageUseCase.execute(slug, imageUrl)) throw ResponseStatusException(NOT_FOUND)
    }

    @PostMapping("/{slug}/translate")
    @ResponseStatus(OK)
    fun translateSynopsis(
        @PathVariable slug: String,
        @CookieValue(value = "user_id", required = false) userId: String? = null,
    ): Map<String, String?> {
        adminAccessChecker.requireAdmin(userId)
        val anime = findAnimeUseCase.execute(slug)
        if (!anime.synopsisPtBr.isNullOrBlank()) {
            return mapOf("synopsisPtBr" to anime.synopsisPtBr, "descriptionPtBr" to anime.descriptionPtBr)
        }
        translateAnimeInfoUserCase.execute(anime)
        val updated = findAnimeUseCase.execute(slug)
        return mapOf("synopsisPtBr" to updated.synopsisPtBr, "descriptionPtBr" to updated.descriptionPtBr)
    }

    @PostMapping("/{slug}/refresh")
    @ResponseStatus(NO_CONTENT)
    fun refreshAnime(
        @PathVariable slug: String,
        @CookieValue(value = "user_id", required = false) userId: String? = null,
    ) {
        adminAccessChecker.requireAdmin(userId)
        if (!refreshAnimeUseCase.execute(slug)) throw ResponseStatusException(NOT_FOUND)
    }

    @PostMapping("/{slug}/alias")
    @ResponseStatus(NO_CONTENT)
    fun addAlias(
        @PathVariable slug: String,
        @RequestBody body: Map<String, String>,
        @CookieValue(value = "user_id", required = false) userId: String? = null,
    ) {
        adminAccessChecker.requireAdmin(userId)
        val rawTitle = body["rawTitle"]?.trim()
        if (rawTitle.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "rawTitle obrigatório")
        val anime = findAnimeUseCase.execute(slug)
        namesRepository.saveConfirmedAlias(rawTitle, anime.id, anime.name)
    }

}