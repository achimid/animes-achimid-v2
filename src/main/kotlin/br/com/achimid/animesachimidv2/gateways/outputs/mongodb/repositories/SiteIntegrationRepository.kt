package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.domains.SiteIntegration
import br.com.achimid.animesachimidv2.domains.SiteIntegrationType.*
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class SiteIntegrationRepository {

    private val db = mutableListOf(
//        SiteIntegration(SLOW, "Animes 365", "https://animes365.net/", getScript("animes365-script.js")),
//        SiteIntegration(SLOW, "Crunchyroll", "https://www.crunchyroll.com/", getScript("crunchyroll-script.js")),
        SiteIntegration(SLOW, "Anitube VIP", "https://www.anitube.vip/", getScript("anitubevip-script.js")),
        SiteIntegration(SLOW, "AnimesRoll", "https://anroll.tv/", getScript("animesroll-script.js"), disableJavaScript = true),
        SiteIntegration(SLOW, "Animes HD", "https://animeshd.to/", getScript("animeshd-to-script.js"), disableJavaScript = true),
        SiteIntegration(SLOW, "Animes Online CC", "https://animesonlinecc.to/episodio/", getScript("animesonlinecc-script.js")),
        SiteIntegration(SLOW, "Goyabu", "https://goyabu.to/lancamentos", getScript("goyabu-script.js"), disableJavaScript = true),
        SiteIntegration(SLOW, "Animes Online FHD", "https://animesonlinefhd.vip/", getScript("animesonlinefhd-script.js")),
        SiteIntegration(SLOW, "Hinata Soul", "https://www.hinatasoul.com/", getScript("hinata-soul.js")),
        SiteIntegration(SLOW, "Anime Q", "https://animeq.blog/", getScript("animeq-script.js")),
        SiteIntegration(SLOW, "Animes Online Cloud", "https://animesonline.cloud/", getScript("animesonlinecloud-script.js")),
        SiteIntegration(SLOW, "Animes Drive", "https://animesdrive.online/episodio", getScript("animesdrive-script.js")),
        SiteIntegration(SLOW, "Animes Up", "https://www.animesup.info/", getScript("animesup-info-script.js"), disableJavaScript = true),
        SiteIntegration(FAST, "Erai-raws (Nyaa)", "https://nyaa.si/?f=0&c=0_0&q=%5BErai-raws%5D+%5B1080p+CR+WEB-DL", getScript("erairaws-script.js")),
        SiteIntegration(FAST, "Subs Please (ENG)", "https://subsplease.org/", getScript("subsplease-script.js")),
        SiteIntegration(FAST, "Dark Animes", "https://darkmahou.org/", getScript("darkanimes-script.js")),
        SiteIntegration(FAST, "Anime Fire", "https://animefire.plus/", getScript("animefire-script.js")),
        SiteIntegration(FAST, "Top Animes", "https://topanimes.net/", getScript("topanimes-script.js")),
    )

    //        SiteIntegration(SLOW, "Central de Animes", "https://centraldeanimes.xyz/", getScript("centraldeanimes-script.js"), disableJavaScript = true),
//    SiteIntegration(SLOW, "Animes Games", "https://animesgames.cc/lancamentos", getScript("animesgames-script.js")),
//        SiteIntegration(SLOW, "Animes Flix", "https://animesflix.net/", getScript("animesflix-script.js")),
//        SiteIntegration(SLOW, "Animes BR", "https://animesbr.tv/episodios/", getScript("animesbr-script.js")),
//        SiteIntegration(SLOW, "Animes Online Red", "https://animesonline.red/", getScript("animesonline-red-script.js")),
//        SiteIntegration(FAST, "Go Animes", "https://www.goanimes.vip/", getScript("goanimes-script.js")),
//        SiteIntegration(FAST, "Bakashi", "https://topanimes.net/", getScript("bakashi-script.js"), enabled = false),

    fun findAll(): List<SiteIntegration> = db.sortedByDescending { it.lastExecutionDate }
    fun findSlow(): List<SiteIntegration> = db.filter { it.type == SLOW }
    fun findMedium(): List<SiteIntegration> = db.filter { it.type == MEDIUM }
    fun findFast(): List<SiteIntegration> = db.filter { it.type == FAST }

    fun findByName(name: String) = db.first { it.name == name }

    private fun getScript(scriptName: String): String {
        val inputStream: InputStream? = object {}.javaClass.getResourceAsStream("/scripts/$scriptName")

        return inputStream!!.bufferedReader().use { it.readText() }
    }

}