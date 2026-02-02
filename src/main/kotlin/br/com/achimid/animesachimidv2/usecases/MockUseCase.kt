package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class MockUseCase {

    val optionsList = listOf(
        EpisodeLinkOptions("#", "Player HD"), EpisodeLinkOptions("#", "Player SD"), EpisodeLinkOptions("#", "Download")
    )

    fun getAnime(): Anime = Anime(
        id = UUID.randomUUID(),
        slug = "sousou-no-frieren",
        title = "Sousou no Frieren",
        detail = AnimeDetail(
            title = "Sousou no Frieren",
            titleSecondary = "葬送 de フリーレン (Frieren: Beyond Journey's End)",
            imageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006.jpg",
            tags = listOf("Ação", "Aventura", "Fantasia", "Isekai", "Reencarnação"),
            description = "A história acompanha a maga elfa Frieren, que após derrotar o Rei Demônio com seu grupo, inicia uma nova jornada para entender a brevidade da vida humana após a morte de seus antigos companheiros.",
            synopsis = "<p>The adventure is over but life goes on for an elf mage who is just beginning to learn what life is all about. Elf mage Frieren and her courageous fellow adventurers have defeated the Demon King and brought peace to the land.</p>\n" +
                    "                <p style=\"margin-top: 10px;\">Decades later, the funeral of one of her friends confronts Frieren with her own near immortality. Frieren sets out to fulfill the last wishes of her comrades and finds herself beginning a new adventure...</p>",
            episodes = listOf(
                EpisodeInfo("Episódio", "01", "O Fim da Jornada", options = optionsList),
                EpisodeInfo("Episódio", "02", "Não Precisa Ser Magia", options = optionsList),
                EpisodeInfo("Episódio", "03", "Zoltraak", options = optionsList),
                EpisodeInfo("Episódio", "08", "Frieren, a Assassina", options = optionsList),
                EpisodeInfo("Episódio", "13", "Aversão à Magia", options = optionsList),
            ),
            infoList = listOf(
                AnimeDetailsInfo("Status", "Ongoing"),
                AnimeDetailsInfo("Studio", "Yokohama Animation Lab"),
                AnimeDetailsInfo("Released", "jan 10, 2026"),
                AnimeDetailsInfo("Duration", "23 min. per ep."),
                AnimeDetailsInfo("Season", "Inverno de 2026"),
                AnimeDetailsInfo("EpisodesCount", "12"),
                AnimeDetailsInfo("Cast", "Chiba Shouya")
            ),
            rankings = RankingStats(
                rating = 9.33, popularity = "#248", rank = "#1"
            ),
        )
    )

    fun getAnimeComment(): AnimeComment = AnimeComment(
        UUID.randomUUID(), listOf(
            Comment(UUID.randomUUID(), "AnimeHunter_99", "AH", "Simplesmente o melhor anime da temporada!"),
            Comment(UUID.randomUUID(), "Fern_Simp", "F", "A evolução da Fern e do Stark é muito fofa...")
        )
    )

    fun getRecommendations(): List<Recommendation> = listOf(
        Recommendation(
            "123",
            "mushoku-tensei",
            "Mushoku Tensei",
            "https://cdn.myanimelist.net/images/anime/1208/94745.jpg",
            "Fantasia / Isekai"
        ), Recommendation(
            "1234",
            "violet-ever",
            "Violet Evergarden",
            "https://cdn.myanimelist.net/images/anime/1160/122627.jpg",
            "Drama / Slice of Life"
        )
    )

    fun getLastReleases(): List<AnimeRelease> = listOf(
        AnimeRelease(animeTitle = "Solo Leveling", animeNumber = "Ep 12"),
        AnimeRelease(animeTitle = "One Piec", animeNumber = "Ep 1105"),
        AnimeRelease(animeTitle = "Ninja Kamui", animeNumber = "Ep 07"),
        AnimeRelease(animeTitle = "Mashle S2", animeNumber = "Ep 10"),
    )

}