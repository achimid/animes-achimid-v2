package br.com.achimid.animesachimidv2.domains

data class AnimeMALIntegration(
    val malId: Int,
    val url: String,
    val detail: List<AnimeMALIntegrationDetails>? = null,
)

data class AnimeMALIntegrationDetails(
    val title: AnimeTitles,
    val mainPicture: ImageSources,
    val trailer: TrailerDetails?,
    val synopsis: String?,
    val background: String?,
    val season: String?,
    val year: Int?,
    val broadcast: String?,
    val status: String?,
    val type: String?,
    val source: String?,
    val episodes: Int?,
    val duration: String?,
    val rating: String?,
    val score: ScoreStatistics,
    val ranking: Int?,
    val popularity: Int?,
    val members: Int?,
    val favorites: Int?,
    val genres: List<EntityReference>,
    val themes: List<EntityReference>,
    val demographics: List<EntityReference>,
    val studios: List<EntityReference>,
    val producers: List<EntityReference>,
    val licensors: List<EntityReference>,
    val relatedAnime: List<RelatedEntry>,
    val characters: List<CharacterRole>,
    val staff: List<StaffRole>,
    val themeSongs: ThemeSongs,
    val externalLinks: List<ExternalLink>
)

data class AnimeTitles(
    val title: String?,
    val english: String?,
    val japanese: String?,
    val synonyms: List<String>
)

data class ImageSources(
    val small: String?,
    val medium: String?,
    val large: String?
)

data class TrailerDetails(
    val youtubeId: String?,
    val url: String?,
    val thumbnailUrl: String?
)

data class ScoreStatistics(
    val score: Double,
    val scoredBy: Int?,
    val distribution: Map<Int, Int>? = null
)

data class EntityReference(
    val id: Int?, // Pode ser 0 ou nulo se o MAL não fornecer link interno
    val name: String,
    val url: String?
)

data class RelatedEntry(
    val relation: String, // ex: "Prequel", "Adaptation"
    val anime: EntityReference
)

data class CharacterRole(
    val character: EntityReference,
    val role: String?, // "Main" ou "Supporting"
    val imageUrl: String?,
    val voiceActors: List<VoiceActor>
)

data class VoiceActor(
    val id: Int?,
    val name: String?,
    val language: String?, // ex: "Japanese"
    val imageUrl: String?
)

data class StaffRole(
    val person: EntityReference,
    val positions: List<String>
)

data class ThemeSongs(
    val openings: List<String>,
    val endings: List<String>
)

data class ExternalLink(
    val name: String,
    val url: String,
    val type: String // Mapeado como String para aceitar o retorno do script ("SOCIAL_MEDIA", etc)
)