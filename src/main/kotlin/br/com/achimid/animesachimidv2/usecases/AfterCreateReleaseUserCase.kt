package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.Anime
import br.com.achimid.animesachimidv2.domains.Release
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class AfterCreateReleaseUserCase(
    private val translateAnimeInfoUserCase: TranslateAnimeInfoUserCase,
    private val notifyFavoritesUseCase: NotifyFavoritesUseCase
) {
    val logger = LoggerFactory.getLogger(this.javaClass)

    @Async
    fun execute(release: Release, anime: Anime, fromSite: String? = null) {
        logger.info("Processing after release creation: ${release.title}")

        // Tradução e notificação são independentes — rodam em paralelo e falhas são isoladas
        val translate = CompletableFuture.runAsync {
            runCatching { translateAnimeInfoUserCase.execute(anime) }
                .onFailure { logger.warn("Falha na tradução de '${anime.name}': ${it.message}") }
        }
        val notify = CompletableFuture.runAsync {
            runCatching { notifyFavoritesUseCase.execute(release, anime, fromSite) }
                .onFailure { logger.warn("Falha ao notificar favoritos de '${anime.name}': ${it.message}") }
        }
        CompletableFuture.allOf(translate, notify).join()
    }
}
