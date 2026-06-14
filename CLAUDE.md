# CLAUDE.md

Guia para o Claude Code (e outros agentes de IA) trabalharem neste repositório.

## O que é o projeto

**Animes Achimid v2** é um agregador de lançamentos de animes. Ele monitora dezenas de
sites de animes, coleta os episódios publicados via um serviço externo de Puppeteer,
enriquece os dados com o MyAnimeList (Jikan API), traduz sinopses para PT-BR e exibe tudo
em um site Thymeleaf + uma API REST.

Aplicação **Spring Boot 4 / Kotlin (JVM 25)** seguindo **Clean Architecture**.

## Comandos essenciais

```bash
# Subir MongoDB local
docker-compose up -d

# Rodar localmente (scraping desabilitado por padrão no profile local)
./gradlew bootRun --args='--spring.profiles.active=local'

# Testes
./gradlew test

# Build do JAR (sem testes)
./gradlew bootJar -x test

# Build da imagem Docker
docker build -t animes-achimid-v2 .
```

- App em `http://localhost:3000`, Swagger UI em `/api`, OpenAPI JSON em `/api-docs`.
  (A porta padrão é `3000`, configurada via `server.port: ${PORT:3000}` em `application.yaml`.)
- O `docker-compose` expõe o MongoDB na porta **27027** (não 27017).
- Profiles: `local` (default) e `prod`. URLs de serviços externos ficam em `application.yaml`;
  overrides por profile em `application-{profile}.yaml`. Dados sensíveis vêm de env vars
  (`MONGODB_URI`, `CALLBACK_URL`).

## Estrutura de pacotes

Raiz: `src/main/kotlin/br/com/achimid/animesachimidv2/`

```
domains/                 # Entidades de negócio puras (sem dependências de framework)
  dto/                   # DTOs auxiliares
  exception/             # Exceções de domínio
usecases/                # Regras de negócio (uma classe por caso de uso)
gateways/
  inputs/http/
    api/                 # @RestController, prefixo /api/v1
      docs/              # Interfaces de documentação OpenAPI
      exception/         # GlobalExceptionHandler
      request/           # DTOs de request (ex.: CallbackIntegration)
    site/                # @Controller que renderiza Thymeleaf
  outputs/
    http/                # Gateways + clientes Feign para APIs externas
      jikan/ subsplease/ puppeteer/ libretranslate/
    mongodb/             # Gateways de persistência
      documents/         # @Document do MongoDB (sufixo Document)
      repositories/      # MongoRepository
      mappers/           # MapStruct (domínio ↔ documento)
cron/                    # Tarefas @Scheduled
configurations/          # @Configuration, filtros, cache, métricas
utils/                   # Funções utilitárias (ex.: StringUtils)

src/main/resources/
  scripts/               # Scripts JS de scraping ({sitename}-script.js)
  templates/             # Thymeleaf (home, animes, anime, calendar, sitemap)
    fragments/ error/
  static/                # css/ js/ img/ favicon/
  application*.yaml
```

## Fluxo de scraping (importante entender)

1. **Cron** (`ExtractionTask`) dispara por fila em ritmos diferentes:
   `FAST` 15min, `MEDIUM` 30min, `SLOW` 60min — mapeados pelo enum `SiteIntegrationType`.
   Há também um job de minuto a minuto que dispara a fila FAST nos horários de lançamento
   do calendário do dia (`executeMatchAnimeReleaseHourMonitoring`).
2. `ExtractionTaskUseCase` busca as `SiteIntegration` da fila e chama `PuppeteerAPIGateway.execute`
   para cada uma.
3. `PuppeteerAPIGateway` envia ao **serviço externo de Puppeteer** a URL + o script JS do site
   + a `callbackUrl`. **O scraping roda fora desta aplicação**, no navegador headless.
4. O Puppeteer chama de volta `POST /api/v1/site/integration/callback` (processado de forma
   assíncrona via `runAsync`).
5. `ProcessIntegrationCallbackUserCase` registra os eventos (deduplicação por `idt`),
   atualiza o status da `SiteIntegration` e cria releases via `CreateReleaseUserCase`.
6. `CreateReleaseUserCase` usa `SearchUseCase` para casar o título raspado com um `Anime`
   (proximidade no Mongo → fuzzy ≥97 → fallback Jikan → fuzzy ≥95), agrupa mirrors no mesmo
   `Release` e salva. `AfterCreateReleaseUserCase` (@Async) dispara a tradução da sinopse.

### Scripts de scraping

- Um arquivo por site em `src/main/resources/scripts/`, nome `{sitename}-script.js`.
- O script roda **no contexto da página** (DOM disponível) e deve **retornar um array** de
  objetos (última expressão do script = valor retornado).
- Formato do objeto:
  ```javascript
  {
    from: "Nome do Site",        // obrigatório
    url: "https://...",          // obrigatório
    title: "Anime - Episódio 5", // obrigatório
    anime: "Anime",              // nome usado no matching
    episode: 5,                  // número do episódio
    languages: ['PT-BR'],        // opcional
    isDub: false,                // opcional
    data: { mirrors: [{ description, url }] } // opcional (espelhos/torrents)
  }
  ```
- Padrão típico: `[...document.querySelectorAll('.seletor')].reverse()` e `console.log`
  para depuração via logs do Puppeteer.
- **Qual site está ativo é controlado pela coleção `site_integrations` no MongoDB**
  (campos `enabled`, `type`, `script`), não pelo código. Adicionar um `.js` aqui não ativa
  nada sozinho.

## Convenções de código

**Nomenclatura de classes** (siga o que já existe no pacote ao criar arquivos novos):
- Casos de uso: sufixo `UseCase` — **atenção:** há inconsistência histórica com `UserCase`
  (ex.: `CreateReleaseUserCase`, `AddFavoriteUserCase`). Prefira `UseCase` em código novo,
  mas confira o nome real ao referenciar os existentes.
- Gateways: sufixo `Gateway` (`AnimeGateway`, `PuppeteerAPIGateway`).
- Controllers de API: sufixo `APIController`; controllers de site: sufixo `Controller`.
- Mappers: sufixo `DocumentMapper`; documentos: sufixo `Document`; configs: sufixo `Config`.

**Estilo Kotlin:**
- Injeção por construtor sempre (sem `@Autowired` em campos).
- Logger por classe: `val logger = LoggerFactory.getLogger(this::class.java)`.
- Encadeamento funcional idiomático (`let`, `also`, `map`, `firstOrNull`) — combine com o estilo local.
- `data class` para domínios e documentos; campos opcionais com defaults.
- Strings voltadas ao usuário são em **português** (o site é PT-BR).

**API:**
- Sempre versione com prefixo `/api/v1/`.
- `@RestController` retorna objetos de domínio direto; `@Controller` de site popula o `Model`
  e retorna o nome do template.
- Páginas de site paralelizam buscas com `CompletableFuture.supplyAsync` + `allOf(...).join()`.

**MongoDB:**
- Coleções no plural (`animes`, `releases`, `site_integrations`).
- Documentos no pacote `documents/`, com `@Document(collection = "...")`.
- `@Indexed(unique = true)` em campos consultados com frequência (ex.: `slug`).
- `auto-index-creation: true` está habilitado.
- Use `@CreatedDate`/`@LastModifiedDate` para timestamps.

**Cache (Caffeine):**
- Caches são declarados explicitamente em `CacheConfig` (nome + TTL + tamanho). Ao usar um
  `@Cacheable("novoCache")` **adicione o cache correspondente em `CacheConfig`**, senão falha.
- Invalide com `@CacheEvict` em escritas (ex.: `CreateReleaseUserCase` evicta `releasesCache`).

**Mappers (MapStruct):**
- Interfaces em `mappers/` com `@Mapper(componentModel = SPRING)`.
- Use `@Mapping(... defaultValue = ...)` para campos ausentes; métodos `default` para lógica
  complexa (ex.: `merge(anime, jikan)` mescla dados do Jikan no documento).

**Integrações externas (Feign):**
- `@FeignClient(name, url = "\${external.X.url}")` em `gateways/outputs/http/{servico}/`.
- Jikan (MyAnimeList), SubsPlease (calendário), LibreTranslate (tradução PT-BR), Puppeteer (scraping).
- Cacheie chamadas externas caras com `@Cacheable`.

**Performance:**
- Virtual threads habilitadas (`spring.threads.virtual.enabled: true`).
- `lazy-initialization` ligado em dev (desligado em prod via `application-prod.yaml`).
- `@Async` para trabalho pós-resposta; `runAsync` para callbacks fire-and-forget.

## Tarefas comuns

**Adicionar um novo site monitorado:**
1. Crie `src/main/resources/scripts/{sitename}-script.js` retornando o array no formato acima.
2. Insira o documento na coleção `site_integrations` (com `name`, `url`, `script`, `type`, `enabled`).
3. Verifique o processamento do callback (logs de `ProcessIntegrationCallbackUserCase`).

**Novo endpoint REST:** crie em `gateways/inputs/http/api/` com prefixo `/api/v1/`, delegando
para um use case. Documente em `docs/` se necessário.

**Novo caso de uso:** uma classe `@Component` em `usecases/` com um método `execute(...)`,
injetando gateways por construtor.

## Cuidados / pontos de atenção

- **Testes praticamente inexistentes** — só há `contextLoads()`. Não assuma cobertura; valide
  manualmente mudanças sensíveis.
- O callback `/api/v1/site/integration/callback` é **público e sem autenticação**.
- Existe um stub vazio `ProcessSiteIntegrationCallbackUseCase` (não confundir com o real
  `ProcessIntegrationCallbackUserCase`).
- O README lista um badge do Google Gemini, mas a tradução em uso é via **LibreTranslate**.
- Veja [MELHORIAS.md](MELHORIAS.md) para dívidas técnicas conhecidas e ideias de evolução.

## CI/CD

- `.github/workflows/docker-ci.yml`: em push/PR na `main`, builda e publica a imagem no
  Docker Hub (tags `latest` e `${{ github.sha }}`), com cache de Gradle e de layers via GHA.
- O build de produção usa o `Dockerfile` multi-stage (Amazon Corretto 25 Alpine).
