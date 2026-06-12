# Animes Achimid v2 - AI Agent Guidelines

## Architecture Overview

This is a Spring Boot Kotlin application following Clean Architecture principles for aggregating anime releases from multiple sources.

**Package Structure:**
```
src/main/
├── kotlin/br/com/achimid/animesachimidv2/
│   ├── domains/           # Core business entities (Anime, Release, SiteIntegration)
│   ├── usecases/          # Application business logic layer
│   ├── gateways/
│   │   ├── inputs/        # Entry points (HTTP controllers)
│   │   │   └── http/
│   │   │       ├── api/   # REST API controllers (@RestController)
│   │   │       └── site/  # Web site controllers (@Controller with Thymeleaf)
│   │   └── outputs/       # External integrations (MongoDB, HTTP clients)
│   │       ├── http/      # External API clients (Jikan, Puppeteer, etc.)
│   │       └── mongodb/   # Database repositories and mappers
│   ├── cron/              # Scheduled background tasks
│   ├── configurations/    # Spring configuration classes
│   └── utils/             # Utility classes
└── resources/
    ├── scripts/           # JavaScript scraping scripts
    ├── static/            # Static resources (CSS, JS, images)
    ├── templates/         # Thymeleaf templates
    └── application*.yaml  # Configuration files
```

**Clean Architecture Layers:**
- **Domains**: Pure business entities with no external dependencies
- **Use Cases**: Business logic orchestrating domain objects
- **Gateways**: Interfaces to external systems (databases, APIs, frameworks)
- **Cron**: Scheduled tasks for automated operations

**Data Flow:**
1. Cron jobs trigger site scraping at different frequencies (FAST: 15min, MEDIUM: 30min, SLOW: 60min)
2. Puppeteer API executes JavaScript scripts against target sites
3. Results sent via callbacks to `ProcessIntegrationCallbackUserCase`
4. Releases created and stored in MongoDB

## Development Workflow

**Local Setup:**
```bash
# Start MongoDB
docker-compose up -d

# Run with local profile (extraction tasks disabled)
./gradlew bootRun --args='--spring.profiles.active=local'

# Enable extraction tasks for testing
extraction-tasks.enabled=true in application-local.yaml
```

**Build & Deploy:**
```bash
# Build JAR
./gradlew bootJar -x test

# Build Docker image
docker build -t animes-achimid-v2 .

# Run with Docker
docker run -p 8080:8080 --env-file .env animes-achimid-v2
```

**Testing:**
- Unit tests use JUnit 5 with Kotlin test extensions
- Integration tests require MongoDB test container
- Run: `./gradlew test`

## Project Rules & Conventions

**API Endpoints:**
- Always use versioned endpoints with `/api/v1/` prefix for REST APIs
- Separate API controllers (`@RestController`) from site controllers (`@Controller`)
- API controllers return domain objects directly, site controllers populate Model for Thymeleaf templates

**Naming Conventions:**
- **Use Cases**: End with `UseCase` (e.g., `FindAnimeUseCase`, `CreateReleaseUserCase`)
- **Gateways**: End with `Gateway` (e.g., `AnimeGateway`, `PuppeteerAPIGateway`)
- **Controllers**: API controllers end with `APIController`, site controllers end with `Controller`
- **Mappers**: End with `DocumentMapper` (e.g., `AnimeDocumentMapper`)
- **Configurations**: End with `Config` (e.g., `CacheConfig`)
- **Documents**: End with `Document` (e.g., `AnimeDocument`)

**File Organization:**
- JavaScript scraping scripts in `src/main/resources/scripts/` with `{sitename}-script.js` naming
- MongoDB documents in `gateways/outputs/mongodb/documents/`
- MapStruct mappers in `gateways/outputs/mongodb/mappers/`
- Spring configurations in `configurations/` package
- HTTP clients in `gateways/outputs/http/` subdirectories by service
- Static resources (CSS, JS, images) in `src/main/resources/static/` organized by type (e.g., css/, js/, img/)
- Thymeleaf templates in `src/main/resources/templates/` with names matching site controller view names (e.g., index.html, anime-details.html)

**Site Resources Conventions:**
- Static files: Organize by type in subdirectories (e.g., css/, js/, img/) and use minified versions for production
- Templates: Use Thymeleaf syntax for dynamic content; name templates to correspond with site controller view names for consistency

**Database Conventions:**
- Collection names use plural form (e.g., `animes`, `releases`, `site_integrations`)
- Document fields use snake_case (e.g., `anime_id`, `last_execution_date`)
- Auto-index creation enabled via `spring.data.mongodb.auto-index-creation: true`
- Use `@Indexed` for frequently queried fields

**Code Structure:**
- Follow Clean Architecture: Domains → Use Cases → Gateways → External systems
- Use constructor injection for dependencies
- Apply caching with `@Cacheable` on expensive operations
- Use virtual threads for async operations where appropriate
- Handle exceptions with custom domain exceptions

**Scraping Integration:**
- Each site has a JavaScript file in `src/main/resources/scripts/` (e.g., `animefire-script.js`)
- Scripts return array of objects with `{from, url, title, anime, episode, languages?, isDub?}` structure
- Site configurations stored in MongoDB via `SiteIntegration` entity
- Scripts executed via external Puppeteer API service
- Use `document.querySelectorAll()` for DOM selection and regex parsing for episode numbers

**Object Mapping:**
- MapStruct mappers in `gateways/outputs/mongodb/mappers/` for domain ↔ document conversion
- Spring component model with `@Mapper(componentModel = SPRING)`
- Custom mapping methods for complex transformations (e.g., Jikan API data merging)
- Default values for missing fields in mapping annotations

**API Integration:**
- Jikan API for MyAnimeList data enrichment
- SubsPlease API for release information
- LibreTranslate API for description translation to Portuguese
- External Puppeteer service for headless browser scraping

**Configuration Management:**
- Environment-based profiles (`local`, `prod`)
- External service URLs in `application.yaml`
- Environment variables for sensitive data (MongoDB URI, API keys)
- Profile-specific overrides in `application-{profile}.yaml`

**Performance & Optimization:**
- Enable virtual threads: `spring.threads.virtual.enabled: true`
- Use lazy initialization in production
- Configure Caffeine caching with appropriate TTL and size limits
- Set Tomcat connection pooling limits appropriately
- Apply `@Cacheable` on read-heavy operations

**Entity Relationships:**
- `Anime` contains episodes and comments (lazy-loaded)
- `Release` links to anime via `animeId`/`animeSlug`
- `SiteIntegration` tracks execution status and timing

## Common Tasks

**Adding New Site Integration:**
1. Create JavaScript scraper in `src/main/resources/scripts/`
2. Add site configuration to MongoDB `site_integrations` collection
3. Test script execution via Puppeteer API
4. Verify callback processing creates releases

**Creating Scraping Scripts:**
- Follow naming convention: `{sitename}-script.js`
- Extract episode data using CSS selectors specific to each site's HTML structure
- Return consistent object format with required fields: `from`, `url`, `title`, `anime`, `episode`
- Example structure:
```javascript
const episodes = [...document.querySelectorAll('.episode-selector')].reverse()
const posts = episodes.map($episode => ({
    from: "Site Name",
    url: $episode.querySelector('a').href,
    title: `${animeName} - Episódio ${episodeNumber}`,
    anime: animeName,
    episode: episodeNumber,
    languages: ['PT-BR'], // optional
    isDub: false // optional
}))
posts // return the array
```

**API Development:**
- REST controllers in `gateways/inputs/http/api/` with `/api/v1/` prefix
- OpenAPI docs available at `/api` endpoint
- Feign clients for external API calls

**Database Operations:**
- Use MongoDB repositories in `gateways/outputs/mongodb/`
- Auto-index creation enabled
- Documents use snake_case field names

**Using MapStruct Mappers:**
- Define interfaces in `gateways/outputs/mongodb/mappers/` with `@Mapper(componentModel = SPRING)`
- Use `@Mapping` annotations for field transformations and default values
- Implement custom methods for complex logic (e.g., merging Jikan data into Anime entities)
