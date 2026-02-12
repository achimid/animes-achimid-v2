package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.SiteIntegrationDocument
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets.UTF_8

@Component
class SiteIntegrationRepository(
    @Qualifier("webApplicationContext") val resourceLoader: ResourceLoader
) {

    fun findAll(): List<SiteIntegrationDocument> = findSlow()
        .plus(findMedium())
        .plus(findFast())
        .sortedByDescending { it.lastExecutionDate }

        fun findSlow(): List<SiteIntegrationDocument> = listOf(
                SiteIntegrationDocument("Crunchyroll", "https://www.crunchyroll.com/", lastExecutionDate = "30/01 22:00", lastExecutionSuccess = true),
                SiteIntegrationDocument("Anitube VIP", "https://www.anitube.vip/", lastExecutionDate = "30/01 22:00"),
                SiteIntegrationDocument("Central de Animes", "https://centraldeanimes.xyz/", ""),
                SiteIntegrationDocument("AnimesRoll", "https://www.anroll.net/lancamentos", ""),
                SiteIntegrationDocument("Animes Games", "https://animesgames.cc/lancamentos", ""),
                SiteIntegrationDocument("Animes Flix", "https://animesflix.net/", ""),
                SiteIntegrationDocument("Animes Online CC", "https://animesonlinecc.to/episodio/", ""),
                SiteIntegrationDocument("Goyabu", "https://goyabu.to/lancamentos", ""),
                SiteIntegrationDocument("Animes BR", "https://animesbr.tv/episodios/", ""),
                SiteIntegrationDocument("Animes Online Red", "https://animesonline.red/", ""),
                SiteIntegrationDocument("Hinata Soul", "https://www.hinatasoul.com/", "", lastExecutionSuccess = true),
                SiteIntegrationDocument("Anime Q", "https://animeq.blog/", "", lastExecutionSuccess = true),
                SiteIntegrationDocument("Animes Online Cloud", "https://animesonline.cloud/", "", lastExecutionSuccess = true),
                SiteIntegrationDocument("Animes Drive", "https://animesdrive.blog/", lastExecutionDate = "30/01 22:00", lastExecutionSuccess = true),
                SiteIntegrationDocument("Animes Up", "https://www.animesup.info/", lastExecutionDate = "30/01 22:00", lastExecutionSuccess = true),

                SiteIntegrationDocument("Erai-raws (Nyaa)", "https://nyaa.si/?f=0&c=0_0&q=%5BErai-raws%5D+%5B1080p+CR+WEB-DL", ""),
                SiteIntegrationDocument("Subs Please (ENG)", "https://subsplease.org/", ""),
                SiteIntegrationDocument("Dark Animes", "https://darkmahou.org/", "", enabled = false),
                SiteIntegrationDocument("Go Animes", "https://www.goanimes.vip/", ""),
                SiteIntegrationDocument("Anime Fire", "https://animefire.plus/", ""),
                SiteIntegrationDocument("Bakashi", "https://q1n.net/", ""),
        )
        fun findMedium(): List<SiteIntegrationDocument> = listOf()
        fun findFast(): List<SiteIntegrationDocument> = listOf(
            SiteIntegrationDocument("Erai-raws (Nyaa)", "https://nyaa.si/?f=0&c=0_0&q=%5BErai-raws%5D+%5B1080p%5D%5BMultiple+Subtitle%5D",  getScript("erairaws-script.js")),
        )

    fun getScript(scriptName: String): String {
        val resource = resourceLoader.getResource("classpath:scripts/$scriptName")

        resource.inputStream.use { inputStream ->
            return String(inputStream.readAllBytes(), UTF_8)
        }
    }

}