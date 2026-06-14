# [Animes Achimid](https://animes.achimid.com.br/)

[![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/spring--boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005C0F?style=for-the-badge&logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)](https://github.com/features/actions)

> Feito por um amante de animes. De fã para fã.

**Animes Achimid** é um agregador de lançamentos de animes da temporada. Ele monitora
dezenas de sites de animes (fansubs, players e trackers de torrent), coleta os episódios
publicados, enriquece os dados com informações do MyAnimeList (via Jikan) e reúne tudo em
um único lugar, com calendário da temporada, busca, recomendações e favoritos.

## ✨ Funcionalidades

- **Agregação de lançamentos** — monitora múltiplas fontes e centraliza os episódios novos.
- **Enriquecimento de dados** — capas, sinopses, notas, gêneros e metadados via [Jikan API](https://jikan.moe/) (MyAnimeList).
- **Tradução automática** — sinopses traduzidas para PT-BR via [LibreTranslate](https://libretranslate.com/) self-hosted.
- **Calendário da temporada** — grade semanal de lançamentos baseada na [SubsPlease](https://subsplease.org/).
- **Busca inteligente** — casamento de títulos por proximidade + _fuzzy matching_ ([FuzzyWuzzy](https://github.com/xdrop/fuzzywuzzy)).
- **Favoritos e comentários** — sem necessidade de cadastro (identificação por cookie).
- **API REST** documentada com OpenAPI/Swagger.
- **SEO** — `sitemap.xml` dinâmico, `robots.txt` e meta tags Open Graph/Twitter.

## 🌐 Links Úteis

- [Site Principal](https://animes.achimid.com.br)
- [Documentação da API (Swagger)](https://animes.achimid.com.br/api)
- [AnimeX](https://animex.achimid.com.br)

## 🏗️ Arquitetura

O projeto segue **Clean Architecture** em Kotlin + Spring Boot. As camadas são organizadas
em pacotes que isolam as regras de negócio das tecnologias externas:

| Camada | Pacote | Responsabilidade |
|---|---|---|
| **Domains** | `domains/` | Entidades de negócio puras (`Anime`, `Release`, `SiteIntegration`, `User`, `Calendar`). |
| **Use Cases** | `usecases/` | Orquestração das regras de negócio. |
| **Gateways (inputs)** | `gateways/inputs/http/` | Controllers REST (`api/`) e do site Thymeleaf (`site/`). |
| **Gateways (outputs)** | `gateways/outputs/` | MongoDB e clientes HTTP externos (Feign). |
| **Cron** | `cron/` | Tarefas agendadas de scraping e sincronização. |
| **Configurations** | `configurations/` | Cache, filtros, métricas e ajustes do Spring. |

### Fluxo de dados (scraping → release)

```
┌──────────┐   1. dispara por fila    ┌─────────────────────┐
│  Cron    │ ───────────────────────► │ ExtractionTaskUseCase│
│ (FAST/   │   (FAST 15m / MED 30m /  └──────────┬──────────┘
│  MED/    │    SLOW 60m)                        │ 2. para cada site
│  SLOW)   │                                     ▼
└──────────┘                          ┌─────────────────────┐
                                      │ PuppeteerAPIGateway │  executa o script JS
                                      │  (serviço externo)  │  do site no headless
                                      └──────────┬──────────┘
                                                 │ 3. callback (async)
                                                 ▼
                              POST /api/v1/site/integration/callback
                                                 │
                                                 ▼
                                ┌────────────────────────────────┐
                                │ ProcessIntegrationCallbackUseCase│
                                └────────────────┬───────────────┘
                                                 │ 4. casa o título com um Anime
                                                 ▼  (SearchUseCase + Jikan + fuzzy)
                                       ┌────────────────────┐
                                       │ CreateReleaseUseCase│ ──► MongoDB (releases)
                                       └────────────────────┘
```

O scraping em si **não roda dentro desta aplicação**: cada site tem um script JavaScript em
[`src/main/resources/scripts/`](src/main/resources/scripts/) que é enviado a um serviço
externo de Puppeteer, executado no navegador headless contra a página alvo, e o resultado
volta por callback. Isso mantém a aplicação leve e isola o scraping.

## 🛠️ Stack

- **Kotlin** (JVM 25) + **Spring Boot 4**
- **MongoDB** (Spring Data) para persistência
- **Thymeleaf** para as páginas do site
- **Spring Cloud OpenFeign** para clientes HTTP externos
- **Caffeine** para cache em memória
- **Resilience4j** para rate limiting
- **MapStruct** para mapeamento domínio ↔ documento
- **FuzzyWuzzy** para busca aproximada de títulos
- **Docker** + **GitHub Actions** para build e deploy

## 🚀 Começando

### Pré-requisitos
- JDK 25
- Docker e Docker Compose

### Rodando localmente

```bash
# 1. Suba o MongoDB local
docker-compose up -d

# 2. Rode a aplicação com o profile local
#    (as tarefas de scraping vêm desabilitadas por padrão no local)
./gradlew bootRun --args='--spring.profiles.active=local'
```

A aplicação sobe em `http://localhost:8080`.

Para testar o scraping localmente, ative `extraction-tasks.enabled: true` em
[`application-local.yaml`](src/main/resources/application-local.yaml).

### Build & Docker

```bash
# Gera o JAR
./gradlew bootJar -x test

# Build da imagem
docker build -t animes-achimid-v2 .

# Executa o container
docker run -p 8080:8080 --env-file .env animes-achimid-v2
```

### Variáveis de ambiente

| Variável | Descrição | Obrigatória |
|---|---|---|
| `MONGODB_URI` | URI de conexão do MongoDB | ✅ |
| `CALLBACK_URL` | URL pública que recebe o callback do Puppeteer | ✅ |
| `SPRING_PROFILES_ACTIVE` | `local` ou `prod` (default: `local`) | — |
| `PORT` | Porta HTTP (default: `8080`) | — |
| `LOG_LEVEL` | Nível de log raiz (default: `INFO`) | — |

## 📡 Endpoints principais

### Site (Thymeleaf)
- `GET /` — Home com lançamentos, recomendações e calendário do dia
- `GET /animes` — Catálogo de animes
- `GET /anime/{slug}` — Página de detalhes do anime
- `GET /calendar` — Calendário da temporada
- `GET /sitemap.xml` — Sitemap dinâmico

### API REST (`/api/v1`)
- `GET /api/v1/animes` — Lista/busca animes paginados
- `GET /api/v1/release` — Lista/busca lançamentos paginados
- `POST /api/v1/anime/{id}/comment` — Adiciona comentário
- `POST` / `DELETE /api/v1/anime/{id}/favorite` — Favorita/desfavorita
- `GET /api/v1/site/integration` — Status das integrações monitoradas
- `POST /api/v1/site/integration/callback` — Callback do Puppeteer (uso interno)

Documentação interativa em `/api` (Swagger UI).

## 📊 Sites Monitorados

### Ativos
- **Erai-raws (Nyaa)**: [https://nyaa.si/](https://nyaa.si/) — torrents de releases em alta qualidade

> Existem **60+ scripts** de scraping em [`src/main/resources/scripts/`](src/main/resources/scripts/)
> para sites já integrados (Goyabu, AnimeFire, Crunchyroll, AnimesDigital, Bakashi, SubsPlease, etc.).
> A ativação de cada um é controlada pela coleção `site_integrations` no MongoDB
> (campos `enabled` e `type`), não pelo código.

## 🤝 Contribuição

1. Faça um fork do projeto.
2. Crie uma branch (`git checkout -b feature/nova-feature`).
3. Commit suas mudanças (`git commit -m 'feat: nova feature'`).
4. Push para a branch (`git push origin feature/nova-feature`).
5. Abra um Pull Request.

### Diretrizes
- Siga os padrões de Clean Architecture e as convenções descritas em [CLAUDE.md](CLAUDE.md).
- Adicione testes para novas funcionalidades.
- Atualize a documentação quando necessário.

## 💡 Roadmap & Ideias

As sugestões de melhorias (técnicas, arquiteturais, performance, SEO e design) foram
movidas para [MELHORIAS.md](MELHORIAS.md). Para novas ideias ou bugs, abra uma
[issue](https://github.com/seu-usuario/animes-achimid-v2/issues).

## 📄 Licença

Distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---

⭐ Se gostou do projeto, deixe uma estrela no GitHub!
