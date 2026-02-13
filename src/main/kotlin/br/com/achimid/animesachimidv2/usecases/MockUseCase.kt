package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.*
import br.com.achimid.animesachimidv2.domains.dto.AnimeCommentDTO
import br.com.achimid.animesachimidv2.domains.dto.AnimeFallowingDTO
import br.com.achimid.animesachimidv2.domains.dto.CommentDTO
import org.springframework.stereotype.Component
import java.util.*

@Component
class MockUseCase {

    val optionsList = listOf(
        EpisodeLinkOptions("https://nyaa.si", "Player HD"),
        EpisodeLinkOptions("#", "Player SD"),
        EpisodeLinkOptions("#", "Download")
    )

    fun getAnimeComment(): AnimeCommentDTO = AnimeCommentDTO(
        listOf(
            CommentDTO(UUID.randomUUID(), "AnimeHunter_99", "AH", "Simplesmente o melhor anime da temporada!"),
            CommentDTO(UUID.randomUUID(), "Fern_Simp", "F", "A evolução da Fern e do Stark é muito fofa...")
        )
    )

    fun getRecommendations(): List<Recommendation> = listOf(
        Recommendation(
            "123",
            "mushoku-tensei",
            "Mushoku Tensei",
            "https://cdn.myanimelist.net/images/anime/1208/94745.jpg",
            "Fantasia / Isekai",
            score = 9.22
        ), Recommendation(
            "1234",
            "violet-ever",
            "Violet Evergarden",
            "https://cdn.myanimelist.net/images/anime/1160/122627.jpg",
            "Drama / Slice of Life",
            score = 9.22
        )
    )

    fun getLastReleases(): List<Release> = listOf(
        Release(
            animeTitle = "Solo Leveling",
            animeNumber = "12",
            options = optionsList,
            animeImageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006.webp"
        ),
        Release(
            animeTitle = "One Piec",
            animeNumber = "1105",
            options = optionsList,
            animeImageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006.webp"
        ),
        Release(
            animeTitle = "Ninja Kamui",
            animeNumber = "07",
            options = optionsList,
            animeImageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006.webp"
        ),
        Release(
            animeTitle = "Mashle S2",
            animeNumber = "10",
            options = optionsList,
            animeImageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006.webp"
        ),
    )

    fun getCalendarRelease(): CalendarRelease = CalendarRelease(
        releasesToday = listOf(
            ScheduleItem("29-sai Dokushin Chuuken Boukensha no Nichijou", "10:00"),
            ScheduleItem("Okiraku Ryoushu no Tanoshii Ryouchi Bouei", "10:30"),
            ScheduleItem("Odayaka Kizoku no Kyuuka no Susume.", "12:00"),
            ScheduleItem("Oshi no Ko S3", "12:00"),
            ScheduleItem("Yuusha Party wo Oidasareta Kiyoubinbou", "12:30"),
            ScheduleItem("Shibou Yuugi de Meshi wo Kuu.", "13:00"),
        )
    )

    fun getFallowing(): List<AnimeFallowingDTO> = listOf(
        AnimeFallowingDTO("Jujutsu Kaisen", "https://cdn.myanimelist.net/images/anime/1127/154488.webp", 100),
        AnimeFallowingDTO("Solo Leveling", "https://cdn.myanimelist.net/images/anime/1864/96171.webp", 50)
    )

    fun getSiteIntegrations(): List<SiteIntegration> = listOf(
        SiteIntegration("Animes Achimid", "", "30/01 22:00", true),
        SiteIntegration("MyAnimeList", "", "30/01 21:45", true),
        SiteIntegration("Crunchyroll", "", "30/01 21:30", true),
        SiteIntegration("AnimeFire", "", "30/01 21:00", false),
        SiteIntegration("BetterAnime", "", "30/01 20:30", true),
    )

}