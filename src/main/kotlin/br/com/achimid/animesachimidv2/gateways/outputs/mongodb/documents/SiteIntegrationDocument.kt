package br.com.achimid.animesachimidv2.gateways.outputs.mongodb.documents

import br.com.achimid.animesachimidv2.domains.SiteIntegrationType
import br.com.achimid.animesachimidv2.domains.SiteIntegrationType.SLOW
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * Persistência das integrações de sites de scraping (F2). O conteúdo do script continua em
 * `resources/scripts/`; aqui guardamos apenas o **nome do arquivo** (`scriptFile`), resolvido
 * na leitura. Habilitar/desabilitar e mudar a fila passam a ser dados (sem redeploy).
 */
@Document(collection = "site_integrations")
data class SiteIntegrationDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val name: String,
    val url: String,
    val type: SiteIntegrationType = SLOW,
    val scriptFile: String? = null,
    val enabled: Boolean = true,
    val skipImage: Boolean = true,
    val disableJavaScript: Boolean? = null,
    val waitTime: Long? = null,
    val lastExecutionDate: Instant? = null,
    val lastExecutionSuccess: Boolean = false,
    val lastExecutionDateWithReleaseSuccess: Instant? = null,
)
