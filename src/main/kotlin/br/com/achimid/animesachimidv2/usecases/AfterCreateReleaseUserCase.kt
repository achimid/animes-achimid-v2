package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.Release
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AfterCreateReleaseUserCase(
    private val translateAnimeInfoUserCase: TranslateAnimeInfoUserCase,
    private val notifyFavoritesUseCase: NotifyFavoritesUseCase
) {
    val logger = LoggerFactory.getLogger(this.javaClass)

    @Async
    fun execute(release: Release, anime: Anime, fromSite: String? = null) {
        logger.info("Processing after release creation: ${release.title}")

        translateAnimeInfoUserCase.execute(anime)

        // FUNC-07: avisa quem favoritou o anime sobre o novo episódio (dedupe por episódio).
        notifyFavoritesUseCase.execute(release, anime, fromSite)
    }

}