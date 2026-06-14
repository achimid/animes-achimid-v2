package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.repositories

import br.com.achimid.animesachimidv2.domains.SiteIntegration
import br.com.achimid.animesachimidv2.domains.SiteIntegrationType
import br.com.achimid.animesachimidv2.domains.SiteIntegrationType.*
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents.SiteIntegrationDocument
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Fachada das integrações de sites (F2). A fonte de verdade agora é a coleção `site_integrations`
 * no MongoDB (via [SiteIntegrationMongoRepository]); este componente apenas converte documento ↔
 * domínio (carregando o conteúdo do script de `resources/scripts/`) e semeia a coleção uma única vez
 * a partir da lista abaixo. Habilitar/desabilitar e mudar a fila passam a ser feitos nos dados.
 */
@Component
class SiteIntegrationRepository(
    private val mongo: SiteIntegrationMongoRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /** Seed inicial: usado apenas quando a coleção está vazia (migração da antiga lista em memória). */
    private val seed = listOf(
        SiteIntegrationDocument(name = "Crunchyroll", url = "https://www.crunchyroll.com/pt-br/simulcastcalendar?filter=premium", type = SLOW, scriptFile = "crunchyroll-script.js"),
        SiteIntegrationDocument(name = "Anitube VIP", url = "https://www.anitube.vip/", type = MEDIUM, scriptFile = "anitubevip-script.js"),
        SiteIntegrationDocument(name = "AnimesRoll", url = "https://anroll.tv/", type = MEDIUM, scriptFile = "animesroll-script.js", disableJavaScript = true),
        SiteIntegrationDocument(name = "Animes HD", url = "https://animeshd.to/", type = MEDIUM, scriptFile = "animeshd-to-script.js", disableJavaScript = true),
        SiteIntegrationDocument(name = "Animes Online CC", url = "https://animesonlinecc.to/episodio/", type = MEDIUM, scriptFile = "animesonlinecc-script.js"),
        SiteIntegrationDocument(name = "Goyabu", url = "https://goyabu.to/lancamentos", type = MEDIUM, scriptFile = "goyabu-script.js", disableJavaScript = true),
        SiteIntegrationDocument(name = "Animes Online FHD", url = "https://animesonlinefhd.vip/", type = MEDIUM, scriptFile = "animesonlinefhd-script.js"),
        SiteIntegrationDocument(name = "Hinata Soul", url = "https://www.hinatasoul.com/", type = MEDIUM, scriptFile = "hinata-soul.js"),
        SiteIntegrationDocument(name = "Anime Q", url = "https://animeq.net/", type = MEDIUM, scriptFile = "animeq-script.js"),
        SiteIntegrationDocument(name = "Animes Online Cloud", url = "https://animesonline.cloud/", type = MEDIUM, scriptFile = "animesonlinecloud-script.js"),
        SiteIntegrationDocument(name = "Animes Drive", url = "https://animesdrive.online/episodio", type = MEDIUM, scriptFile = "animesdrive-script.js"),
        SiteIntegrationDocument(name = "Animes Up", url = "https://www.animesup.info/", type = MEDIUM, scriptFile = "animesup-info-script.js", disableJavaScript = true),
        SiteIntegrationDocument(name = "Erai-raws (Nyaa)", url = "https://nyaa.si/?f=0&c=0_0&q=%5BErai-raws%5D+%5B1080p", type = FAST, scriptFile = "erairaws-script.js"),
        SiteIntegrationDocument(name = "Subs Please (ENG)", url = "https://subsplease.org/", type = FAST, scriptFile = "subsplease-script.js"),
        SiteIntegrationDocument(name = "Dark Animes", url = "https://darkmahou.io", type = FAST, scriptFile = "darkanimes-script.js"),
        SiteIntegrationDocument(name = "Anime Fire", url = "https://animefire.plus/", type = FAST, scriptFile = "animefire-script.js"),
        SiteIntegrationDocument(name = "Top Animes", url = "https://topanimes.net/", type = FAST, scriptFile = "topanimes-script.js"),
        SiteIntegrationDocument(name = "World Fansub", url = "https://worldfansub.xyz/#lancamentos ", type = SLOW, scriptFile = "worldfansub-script.js", disableJavaScript = true),
        SiteIntegrationDocument(name = "AnimeFlix (ENG)", url = "https://animeflix.team/", type = MEDIUM, scriptFile = "animeflix-script.js", disableJavaScript = true, waitTime = 300),
        SiteIntegrationDocument(name = "Anime NSK", url = "https://packs.ansktracker.com/", type = SLOW, scriptFile = "animensk-script.js"),
        SiteIntegrationDocument(name = "Animes Digital", url = "https://animesdigital.org/lancamentos", type = MEDIUM, scriptFile = "animesdigital-script.js"),
        SiteIntegrationDocument(name = "Better Anime IO", url = "https://betteranime.io/home/", type = FAST, scriptFile = "betteranimeio-script.js"),
        SiteIntegrationDocument(name = "Sushi Animes", url = "https://sushianimes.com.br/episodios", type = FAST, scriptFile = "sushianimes-script.js"),
        SiteIntegrationDocument(name = "AnimesBR Lat", url = "https://animesbr.lat/episodios", type = FAST, scriptFile = "animesbrlat-script.js", waitTime = 300),

        // Candidatos para triagem (FUNC-14): scripts já existem no repo, mas precisam de validação.
        // Entram DESABILITADOS — o admin valida e habilita pelo painel (/admin).
        SiteIntegrationDocument(name = "Animes 365", url = "https://animes365.net/", type = SLOW, scriptFile = "animes365-script.js", enabled = false),
        SiteIntegrationDocument(name = "Central de Animes", url = "https://centraldeanimes.xyz/", type = SLOW, scriptFile = "centraldeanimes-script.js", disableJavaScript = true, enabled = false),
        SiteIntegrationDocument(name = "Animes Games", url = "https://animesgames.cc/lancamentos", type = SLOW, scriptFile = "animesgames-script.js", enabled = false),
        SiteIntegrationDocument(name = "Animes Flix", url = "https://animesflix.net/", type = SLOW, scriptFile = "animesflix-script.js", enabled = false),
        SiteIntegrationDocument(name = "Animes BR", url = "https://animesbr.tv/episodios/", type = SLOW, scriptFile = "animesbr-script.js", enabled = false),
        SiteIntegrationDocument(name = "Animes Online Red", url = "https://animesonline.red/", type = SLOW, scriptFile = "animesonline-red-script.js", enabled = false),
        SiteIntegrationDocument(name = "Go Animes", url = "https://www.goanimes.vip/", type = SLOW, scriptFile = "goanimes-script.js", enabled = false),
        SiteIntegrationDocument(name = "Bakashi", url = "https://bakashi.tv/", type = SLOW, scriptFile = "bakashi-script.js", enabled = false),
    )

    /**
     * Insere os sites do seed que ainda não existem na coleção (idempotente por nome).
     * Assim, novos sites adicionados ao seed (FUNC-14) propagam para bancos já populados,
     * sem sobrescrever ajustes do admin (enabled/fila) nos sites existentes.
     */
    @PostConstruct
    fun seedMissing() {
        val existingNames = mongo.findAll().map { it.name }.toSet()
        val missing = seed.filterNot { it.name in existingNames }
        if (missing.isNotEmpty()) {
            mongo.saveAll(missing)
            logger.info("Seed: ${missing.size} integrações de sites inseridas no MongoDB")
        }
    }

    fun findAll(): List<SiteIntegration> = mongo.findAll().map(::toDomain).sortedWith(
        compareByDescending<SiteIntegration> { it.lastExecutionSuccess }
            .thenByDescending { it.lastExecutionDateWithReleaseSuccess }
            .thenBy { it.lastExecutionDate }
    )

    fun findSlow(): List<SiteIntegration> = mongo.findByTypeAndEnabledTrue(SLOW).map(::toDomain)
    fun findMedium(): List<SiteIntegration> = mongo.findByTypeAndEnabledTrue(MEDIUM).map(::toDomain)
    fun findFast(): List<SiteIntegration> = mongo.findByTypeAndEnabledTrue(FAST).map(::toDomain)

    fun findByName(name: String): SiteIntegration =
        toDomain(mongo.findByName(name) ?: error("SiteIntegration não encontrada: $name"))

    /** Habilita/desabilita um site (FUNC-04). */
    fun setEnabled(name: String, enabled: Boolean): Boolean {
        val doc = mongo.findByName(name) ?: return false
        mongo.save(doc.copy(enabled = enabled))
        return true
    }

    /** Move um site para outra fila FAST/MEDIUM/SLOW (FUNC-04). */
    fun setType(name: String, type: SiteIntegrationType): Boolean {
        val doc = mongo.findByName(name) ?: return false
        mongo.save(doc.copy(type = type))
        return true
    }

    /** Persiste o resultado da última execução (corrige o bug do antigo update em memória). */
    fun updateExecution(name: String, success: Boolean, withRelease: Boolean) {
        val doc = mongo.findByName(name) ?: return
        val now = Instant.now()
        mongo.save(
            doc.copy(
                lastExecutionSuccess = success,
                lastExecutionDate = now,
                lastExecutionDateWithReleaseSuccess = if (withRelease) now else doc.lastExecutionDateWithReleaseSuccess
            )
        )
    }

    private fun toDomain(doc: SiteIntegrationDocument) = SiteIntegration(
        type = doc.type,
        name = doc.name,
        url = doc.url,
        script = doc.scriptFile?.let(::getScript),
        enabled = doc.enabled,
        skipImage = doc.skipImage,
        disableJavaScript = doc.disableJavaScript,
        waitTime = doc.waitTime,
        lastExecutionDate = doc.lastExecutionDate,
        lastExecutionSuccess = doc.lastExecutionSuccess,
        lastExecutionDateWithReleaseSuccess = doc.lastExecutionDateWithReleaseSuccess,
    )

    private fun getScript(scriptName: String): String? {
        val inputStream = object {}.javaClass.getResourceAsStream("/scripts/$scriptName")
        return inputStream?.bufferedReader()?.use { it.readText() }
    }
}
