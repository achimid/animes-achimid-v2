package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.MonitoredSiteDocument
import org.springframework.stereotype.Component

@Component
class MonitoredSitesRepository {

    fun findAll(): List<MonitoredSiteDocument> = listOf(
            MonitoredSiteDocument("Crunchyroll", "https://www.crunchyroll.com/", lastExecutionDate = "30/01 22:00", lastExecutionSuccess = true),
            MonitoredSiteDocument("Anitube VIP", "https://www.anitube.vip/", lastExecutionDate = "30/01 22:00"),
            MonitoredSiteDocument("Central de Animes", "https://centraldeanimes.xyz/", ""),
            MonitoredSiteDocument("AnimesRoll", "https://www.anroll.net/lancamentos", ""),
            MonitoredSiteDocument("Animes Games", "https://animesgames.cc/lancamentos", ""),
            MonitoredSiteDocument("Animes Flix", "https://animesflix.net/", ""),
            MonitoredSiteDocument("Animes Online CC", "https://animesonlinecc.to/episodio/", ""),
            MonitoredSiteDocument("Goyabu", "https://goyabu.to/lancamentos", ""),
            MonitoredSiteDocument("Animes BR", "https://animesbr.tv/episodios/", ""),
            MonitoredSiteDocument("Animes Online Red", "https://animesonline.red/", ""),
            MonitoredSiteDocument("Hinata Soul", "https://www.hinatasoul.com/", "", lastExecutionSuccess = true),
            MonitoredSiteDocument("Anime Q", "https://animeq.blog/", "", lastExecutionSuccess = true),
            MonitoredSiteDocument("Animes Online Cloud", "https://animesonline.cloud/", "", lastExecutionSuccess = true),
            MonitoredSiteDocument("Animes Drive", "https://animesdrive.blog/", lastExecutionDate = "30/01 22:00", lastExecutionSuccess = true),
            MonitoredSiteDocument("Animes Up", "https://www.animesup.info/", lastExecutionDate = "30/01 22:00", lastExecutionSuccess = true),
            MonitoredSiteDocument("Erai-raws (Nyaa)", "https://nyaa.si/?f=0&c=0_0&q=%5BErai-raws%5D+%5B1080p%5D%5BMultiple+Subtitle%5D", ""),
            MonitoredSiteDocument("Erai-raws (Nyaa)", "https://nyaa.si/?f=0&c=0_0&q=%5BErai-raws%5D+%5B1080p+CR+WEB-DL", ""),
            MonitoredSiteDocument("Subs Please (ENG)", "https://subsplease.org/", ""),
            MonitoredSiteDocument("Dark Animes", "https://darkmahou.org/", "", enabled = false),
            MonitoredSiteDocument("Go Animes", "https://www.goanimes.vip/", ""),
            MonitoredSiteDocument("Anime Fire", "https://animefire.plus/", ""),
            MonitoredSiteDocument("Bakashi", "https://q1n.net/", ""),
        ).sortedByDescending { it.lastExecutionDate }

}