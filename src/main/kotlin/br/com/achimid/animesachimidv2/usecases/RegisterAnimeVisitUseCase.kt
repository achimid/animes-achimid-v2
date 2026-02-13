package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories.old.AnimeRepository
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.runAsync

@Component
class RegisterAnimeVisitUseCase(
    val animeRepository: AnimeRepository,
) {

    fun execute(id: String): CompletableFuture<Void> = runAsync { animeRepository.incrementAccessCounter(id) }

}