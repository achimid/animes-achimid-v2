package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.dto.AnimeFallowingDTO
import org.springframework.stereotype.Component

@Component
class MockUseCase {

    fun getFallowing(): List<AnimeFallowingDTO> = listOf(
        AnimeFallowingDTO("Jujutsu Kaisen", "https://cdn.myanimelist.net/images/anime/1127/154488.webp", 100),
        AnimeFallowingDTO("Solo Leveling", "https://cdn.myanimelist.net/images/anime/1864/96171.webp", 50)
    )

}