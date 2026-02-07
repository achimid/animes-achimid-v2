package br.com.achimid.animesachimidv2.usecases

import br.com.achimid.animesachimidv2.domains.dto.AnimeDTO
import br.com.achimid.animesachimidv2.domains.dto.AnimeCommentDTO
import br.com.achimid.animesachimidv2.domains.dto.AnimeDetailDTO
import br.com.achimid.animesachimidv2.domains.dto.AnimeDetailTitleDTO
import br.com.achimid.animesachimidv2.domains.dto.AnimeDetailsInfoDTO
import br.com.achimid.animesachimidv2.domains.dto.AnimeFallowingDTO
import br.com.achimid.animesachimidv2.domains.dto.AnimeReleaseResponse
import br.com.achimid.animesachimidv2.domains.dto.CalendarItemDTO
import br.com.achimid.animesachimidv2.domains.dto.CalendarRelease
import br.com.achimid.animesachimidv2.domains.dto.CommentDTO
import br.com.achimid.animesachimidv2.domains.dto.EpisodeInfoDTO
import br.com.achimid.animesachimidv2.domains.dto.EpisodeLinkOptionsDTO
import br.com.achimid.animesachimidv2.domains.dto.RankingStatsDTO
import br.com.achimid.animesachimidv2.domains.dto.RecommendationDTO
import br.com.achimid.animesachimidv2.domains.MonitoredSite
import org.springframework.stereotype.Component
import java.util.*

@Component
class MockUseCase {

    val optionsList = listOf(
        EpisodeLinkOptionsDTO("https://nyaa.si", "Player HD"),
        EpisodeLinkOptionsDTO("#", "Player SD"),
        EpisodeLinkOptionsDTO("#", "Download")
    )

    fun getAnime(): AnimeDTO = AnimeDTO(
        slug = "sousou-no-frieren-1-season",
        title = "Sousou no Frieren",
        episodes = listOf(
            EpisodeInfoDTO("Episódio", "01", "O Fim da Jornada", options = optionsList),
            EpisodeInfoDTO("Episódio", "02", "Não Precisa Ser Magia", options = optionsList),
            EpisodeInfoDTO("Episódio", "03", "Zoltraak", options = optionsList),
            EpisodeInfoDTO("Episódio", "08", "Frieren, a Assassina", options = optionsList),
            EpisodeInfoDTO("Episódio", "13", "Aversão à Magia", options = optionsList),
        ),
        detail = AnimeDetailDTO(
            titles = AnimeDetailTitleDTO("Sousou no Frieren", "葬送 de フリーレン (Frieren: Beyond Journey's End)"),
            imageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006.jpg",
            tags = listOf("Ação", "Aventura", "Fantasia", "Isekai", "Reencarnação"),
            description = "A história acompanha a maga elfa Frieren, que após derrotar o Rei Demônio com seu grupo, inicia uma nova jornada para entender a brevidade da vida humana após a morte de seus antigos companheiros.",
            synopsis = "<p>The adventure is over but life goes on for an elf mage who is just beginning to learn what life is all about. Elf mage Frieren and her courageous fellow adventurers have defeated the Demon King and brought peace to the land.</p>\n" +
                    "                <p style=\"margin-top: 10px;\">Decades later, the funeral of one of her friends confronts Frieren with her own near immortality. Frieren sets out to fulfill the last wishes of her comrades and finds herself beginning a new adventure...</p>",
            infoList = listOf(
                AnimeDetailsInfoDTO("Status", "Ongoing"),
                AnimeDetailsInfoDTO("Studio", "Yokohama Animation Lab"),
                AnimeDetailsInfoDTO("Released", "jan 10, 2026"),
                AnimeDetailsInfoDTO("Duration", "23 min. per ep."),
                AnimeDetailsInfoDTO("Season", "Inverno de 2026"),
                AnimeDetailsInfoDTO("Episodes", "12"),
                AnimeDetailsInfoDTO("Cast", "Chiba Shouya")
            ),
            rankings = RankingStatsDTO(
                rating = 9.33, popularity = "#248", rank = "#1"
            ),
        )
    )

    fun getCatalogList(): List<AnimeDTO> = listOf(getAnime(), getAnime(), getAnime(), getAnime(), getAnime())

    fun getAnimeComment(): AnimeCommentDTO = AnimeCommentDTO(
        listOf(
            CommentDTO(UUID.randomUUID(), "AnimeHunter_99", "AH", "Simplesmente o melhor anime da temporada!"),
            CommentDTO(UUID.randomUUID(), "Fern_Simp", "F", "A evolução da Fern e do Stark é muito fofa...")
        )
    )

    fun getRecommendations(): List<RecommendationDTO> = listOf(
        RecommendationDTO(
            "123",
            "mushoku-tensei",
            "Mushoku Tensei",
            "https://cdn.myanimelist.net/images/anime/1208/94745.jpg",
            "Fantasia / Isekai",
            rating = 9.22
        ), RecommendationDTO(
            "1234",
            "violet-ever",
            "Violet Evergarden",
            "https://cdn.myanimelist.net/images/anime/1160/122627.jpg",
            "Drama / Slice of Life",
            rating = 9.22
        )
    )

    fun getLastReleases(): List<AnimeReleaseResponse> = listOf(
        AnimeReleaseResponse(
            animeTitle = "Solo Leveling",
            animeNumber = "12",
            options = optionsList,
            animeImageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006.webp"
        ),
        AnimeReleaseResponse(
            animeTitle = "One Piec",
            animeNumber = "1105",
            options = optionsList,
            animeImageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006.webp"
        ),
        AnimeReleaseResponse(
            animeTitle = "Ninja Kamui",
            animeNumber = "07",
            options = optionsList,
            animeImageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006.webp"
        ),
        AnimeReleaseResponse(
            animeTitle = "Mashle S2",
            animeNumber = "10",
            options = optionsList,
            animeImageUrl = "https://cdn.myanimelist.net/images/anime/1015/138006.webp"
        ),
    )

    fun getCalendarRelease(): CalendarRelease = CalendarRelease(
        releasesToday = listOf(
            CalendarItemDTO("29-sai Dokushin Chuuken Boukensha no Nichijou", "10:00", true),
            CalendarItemDTO("Okiraku Ryoushu no Tanoshii Ryouchi Bouei", "10:30", true),
            CalendarItemDTO("Odayaka Kizoku no Kyuuka no Susume.", "12:00", true),
            CalendarItemDTO("Oshi no Ko S3", "12:00", false),
            CalendarItemDTO("Yuusha Party wo Oidasareta Kiyoubinbou", "12:30", false),
            CalendarItemDTO("Shibou Yuugi de Meshi wo Kuu.", "13:00", false),
        )
    )

    fun getFallowing(): List<AnimeFallowingDTO> = listOf(
        AnimeFallowingDTO("Jujutsu Kaisen", "https://cdn.myanimelist.net/images/anime/1127/154488.webp", 100),
        AnimeFallowingDTO("Solo Leveling", "https://cdn.myanimelist.net/images/anime/1864/96171.webp", 50)
    )

    fun getSitesMonitored(): List<MonitoredSite> = listOf(
        MonitoredSite("Animes Achimid", "", "30/01 22:00", true),
        MonitoredSite("MyAnimeList", "", "30/01 21:45", true),
        MonitoredSite("Crunchyroll", "", "30/01 21:30", true),
        MonitoredSite("AnimeFire", "", "30/01 21:00", false),
        MonitoredSite("BetterAnime", "", "30/01 20:30", true),
    )

}