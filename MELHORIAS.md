# Sugestões de Melhoria — Animes Achimid v2

> Documento de análise técnica gerado em **2026-06-12** a partir de leitura dirigida do código (não de execução). Reúne oportunidades em **bugs/robustez, segurança, performance, arquitetura/qualidade, SEO, design/UX, build e funcionalidades**. É um **backlog de evolução** — nenhuma alteração de código foi feita ao gerá-lo.

## Como ler

- A **tabela de priorização** abaixo é o índice. Cada linha tem um **ID** que aponta para o detalhe no catálogo.
- No catálogo, cada item traz **Onde** (`arquivo:linha`), **Problema**, **Impacto** e **Recomendação**; os mais relevantes têm uma subseção **Como corrigir (detalhado)** com abordagens (A/B/C), a recomendada (✅) e os **passos**.
- **Severidade:** `ALTA` (bug ativo / risco real / pré-requisito de produção) · `MÉDIA` (dívida que escala mal) · `BAIXA` (polimento / consistência).
- **Esforço:** estimativa grosseira — `P` (pequeno, < meio dia) · `M` (médio, 1-3 dias) · `G` (grande, > 3 dias / requer infra ou rede de testes).
- Ícones: 🐞 bug provável · ⚡ performance · 🔒 segurança · 🎨 design/UX · 🔎 SEO · 🧱 arquitetura.
- Itens marcados com 🔒 são **pré-requisitos de exposição pública** — o callback e a identidade por cookie são abertos por design hoje.

> **Base que já está boa (não regredir):** Clean Architecture coerente; cache Caffeine declarado com TTLs por uso; virtual threads ligadas; compressão + `Cache-Control` de 30 dias em prod; SEO on-page decente (`lang`, `<title>`/`description`/`canonical`/OpenGraph por página, JSON-LD `TVSeries`, `sitemap.xml`, `robots.txt`); dedup de eventos com TTL de 3 dias; rate limiter no disparo manual de extração.

---

## Tabela de priorização

| ID | Item | Área | Sev. | Esf. |
|----|------|------|:----:|:----:|
| **BUG-1** 🐞 | `updateByName` nunca chama `save()` — status de integração não persiste | Pipeline | ALTA | P |
| **BUG-2** 🐞 | `createEventIntegration` retorna `true` ao falhar (release a partir de evento não salvo) | Pipeline | ALTA | P |
| **BUG-3** 🐞 | `findRandom` usa página aleatória fixa `(0..300)` — recomendações vazias | Catálogo | ALTA | P |
| **BUG-4** 🐞 | `getAllNames`: cast inseguro `as HashMap` + `!!` + `catch` engolido | Catálogo | ALTA | P |
| **BUG-5** 🐞 | Contador de visitas com regex que não casa as rotas reais | Métricas | MÉDIA | P |
| **BUG-6** 🐞 | `ReleaseGateway.fromDocument` cheio de `!!` (NPE em dados legados) | Persistência | MÉDIA | P |
| **SEC-1** 🔒 | Callback de integração público e sem autenticação (forja de releases) | Segurança | ALTA | M |
| **SEC-2** 🔒 | `POST /extraction/all/run` público (dispara scraping de toda a fila) | Segurança | ALTA | P |
| **SEC-3** 🔒 | Identidade por cookie `user_id` sem assinatura; `isAdmin` confiável no cliente | Segurança | ALTA | M |
| **SEC-4** 🔒 | `th:utext` na sinopse + comentários sem sanitização (XSS) | Segurança | ALTA | M |
| **SEC-5** 🔒 | Sem headers de segurança / CSP | Segurança | MÉDIA | M |
| **PIPE-1** | Dedup por `idt` com índice não-único (corrida) e `idt` frágil | Pipeline | MÉDIA | M |
| **PERF-1** ⚡ | Sem índice composto `releases(animeId, episode)` — full scan por release | Performance | MÉDIA | P |
| **PERF-2** ⚡ | `supplyAsync` usa ForkJoinPool comum, não as virtual threads | Performance | MÉDIA | P |
| **PERF-3** ⚡ | Sem rate limit na Jikan + N+1 de chamadas no calendário | Performance | MÉDIA | M |
| **PERF-4** ⚡ | Estatísticas de cache coletadas mas não publicadas | Performance | BAIXA | P |
| **ARCH-1** 🧱 | `UseCase` vs `UserCase` (typo histórico) inconsistente | Arquitetura | BAIXA | M |
| **ARCH-2** 🧱 | Lombok num projeto 100% Kotlin | Arquitetura | BAIXA | P |
| **ARCH-3** 🧱 | Vazamento de camada: DTO de request HTTP até persistência | Arquitetura | MÉDIA | M |
| **ARCH-4** 🧱 | `GlobalExceptionHandler` vazio + `catch (RuntimeException)` amplo | Arquitetura | MÉDIA | M |
| **ARCH-5** 🧱 | Código morto: stub vazio + `Resilience4jConfig` vazio + typo `createEvenIntegration` | Arquitetura | BAIXA | P |
| **SEO-1** 🔎 | `sitemap.xml` sem `<lastmod>` e com prioridades estáticas | SEO | MÉDIA | P |
| **SEO-2** 🔎 | Domínio canônico hardcoded nos templates | SEO | BAIXA | P |
| **SEO-3** 🔎 | JSON-LD com `ratingCount: 10000` fixo (sinal enganoso) | SEO | MÉDIA | P |
| **SEO-4** 🔎 | Core Web Vitals: imagens sem `lazy`/dimensões, fontes render-blocking | SEO | MÉDIA | M |
| **UX-1** 🎨 | Sem dark mode (`prefers-color-scheme`) | Design | BAIXA | M |
| **UX-2** 🎨 | Sem skeleton/estados de carregamento nas listas assíncronas | Design | BAIXA | M |
| **UX-3** 🎨 | Acessibilidade: contraste, `alt`, teclado, `aria-label` | Design | MÉDIA | M |
| **UX-4** 🎨 | PWA incompleto (manifest sem service worker) | Design | BAIXA | M |
| **BUILD-1** | Sem pipeline de build de frontend (minify/bundle/hash) | Build | MÉDIA | M |
| **BUILD-2** | CI só builda imagem; sem testes nem lint | Build | MÉDIA | P |
| **TEST-1** | Cobertura ~zero (só `contextLoads`) | Qualidade | ALTA | G |
| **OBS-1** | Actuator no classpath, mas sem exposição/métricas configuradas | Observ. | MÉDIA | P |
| **OBS-2** | Sem healthcheck no Docker / readiness-liveness | Observ. | BAIXA | P |
| **FEAT-1** | `languages`/`isDub` chegam no callback mas não são persistidos | Produto | MÉDIA | M |
| **FEAT-2** | Painel admin para habilitar sites/editar scripts | Produto | — | G |
| **FEAT-3** | Notificações de lançamento para favoritos | Produto | — | G |
| **FEAT-4** | Filtros no catálogo (tipo, status, gênero, nota) | Produto | — | M |

---

## A. Bugs e robustez (alta prioridade)

> ⚠️ **Achado estrutural que afeta vários itens:** apesar do `CLAUDE.md` afirmar que "qual site está ativo é controlado pela coleção `site_integrations` no MongoDB", o [SiteIntegrationRepository](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/repositories/SiteIntegrationRepository.kt#L9-L37) é, na verdade, uma **lista `mutableListOf` hardcoded em memória** — não um `MongoRepository`. Adicionar/remover/ligar um site exige **editar o código e redeploy**. Isso reposiciona [FEAT-2](#feat-2--painel-admin-para-sites-e-scripts) (painel admin) de "nice-to-have" para "destrava operação" e explica a natureza de **BUG-1**. Vale corrigir a documentação ou migrar de fato para o Mongo.

### BUG-1 · `updateByName` não persiste fora da memória + ignora `enabled` + estoura em `ref` desconhecido 🐞 — ALTA
- **Onde:** [SiteIntegrationGateway.kt:30-36](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/SiteIntegrationGateway.kt#L30-L36) e [SiteIntegrationRepository.kt:65-69](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/repositories/SiteIntegrationRepository.kt#L65-L69).
- **Problema:** Três defeitos no mesmo ponto:
  1. `updateByName` muta `lastExecutionSuccess`/`lastExecutionDate` no objeto retornado por `findByName`. Como o objeto é a **mesma referência** da lista em memória, a mutação "funciona" — mas é **volátil** (perde-se no restart) e **não compartilhada entre réplicas**. Não há nenhum `save()` para o Mongo. A documentação diz que esse estado vive no banco; não vive.
  2. `findFast/Medium/Slow` ([:65-67](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/repositories/SiteIntegrationRepository.kt#L65-L67)) **não filtram por `enabled`** — o flag `enabled` do domínio é morto na prática (só comentar a linha desliga um site).
  3. `findByName` usa `db.first { it.name == name }` ([:69](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/repositories/SiteIntegrationRepository.kt#L69)): se um callback chega com um `ref` que não casa nenhum site (ex.: payload forjado — ver [SEC-1](#sec-1--callback-de-integração-público-e-sem-autenticação)), lança `NoSuchElementException`.
- **Impacto:** O dashboard de status das integrações reflete estado que zera a cada deploy e diverge entre instâncias; `enabled=false` não desliga nada; um `ref` inválido derruba o processamento daquele callback (engolido pelo `runAsync`, some sem rastro).
- **Recomendação:** Decidir a fonte da verdade. Se o estado de execução deve sobreviver a restart/escalar, persistir de fato (migrar `SiteIntegration` para Mongo ou gravar só o status de execução numa coleção). No mínimo: filtrar por `enabled` e trocar `first` por `firstOrNull` com log.

#### Como corrigir (detalhado)

São três correções; (2) e (3) são triviais e devem entrar já. A (1) é uma decisão de arquitetura.

- **(3) `firstOrNull` + log:** trocar `db.first {…}` por `firstOrNull` e, no gateway, tratar `null` com `warn` ("callback para site desconhecido: $name") em vez de propagar exceção. Barato, fecha o vetor de crash do callback aberto.
- **(2) Respeitar `enabled`:** adicionar `.filter { it.enabled }` em `findFast/Medium/Slow` (e em `findAll` se a tela não quiser ver desabilitados). Assim o flag deixa de ser decorativo.
- **(1) Persistência do estado de execução — abordagens:**
  - **Abordagem A — migrar `SiteIntegration` para uma coleção Mongo de verdade.** A lista hardcoded vira seed/migração; `enabled`/`type`/`script` passam a ser dados. Casa com [FEAT-2](#feat-2--painel-admin-para-sites-e-scripts) (editar pelo admin) e alinha o código ao que o `CLAUDE.md` já descreve. *Prós:* fonte única, persistente, escalável, editável sem deploy. *Contras:* esforço maior (migração + seed + mapper/documento); cuidado para não perder os scripts versionados em `resources/scripts/`.
  - **Abordagem B — manter a lista em memória, persistir só o status de execução.** Uma coleção `site_integration_status` (`name`, `lastExecutionDate`, `lastExecutionSuccess`, `lastExecutionDateWithReleaseSuccess`) atualizada por `updateByName`; a tela mescla a lista estática + status do Mongo. *Prós:* menor mudança, resolve a volatilidade; *Contras:* dois lugares de verdade (config no código, status no banco).
  - **Abordagem C — só documentar que é volátil/in-memory.** Corrigir o `CLAUDE.md` e aceitar a perda no restart. *Prós:* zero código; *Contras:* mantém a divergência entre réplicas e a falsa impressão de persistência.
- **✅ Recomendado:** **A** como destino (destrava FEAT-2 e bate com a doc), com **(2) e (3) imediatos** independentemente. Se A for adiada, fazer **B** para ao menos não perder o status no deploy; em último caso, **C** apenas corrige a documentação.
- **Passos:** 1) `firstOrNull`+log; 2) filtrar `enabled`; 3) decidir A/B; 4) se A, criar `SiteIntegrationDocument`+repository+mapper, seed a partir da lista atual, ler `script` do recurso; 5) atualizar o `CLAUDE.md`.

### BUG-2 · `createEventIntegration` retorna `true` ao falhar 🐞 — ALTA
- **Onde:** [ProcessIntegrationCallbackUserCase.kt:38-44](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/ProcessIntegrationCallbackUserCase.kt#L38-L44).
- **Problema:** O `catch (_: RuntimeException) { true }` faz um evento que **falhou ao salvar** ser tratado como "novo" e seguir para a criação de release. A deduplicação (que devolve `false` para evento já visto) tem a semântica invertida no caminho de erro: erro vira "prossiga".
- **Impacto:** Em falha transitória do Mongo, cria-se release sem o evento correspondente registrado (a dedup por `idt` deixa de proteger), podendo gerar duplicatas no próximo callback do mesmo episódio.
- **Recomendação:** No erro, retornar `false` (pular com segurança) ou re-lançar para o `catch` externo abortar o item. Logar o erro — hoje é silencioso.

### BUG-3 · `findRandom` usa página aleatória fixa `(0..300)` 🐞 — ALTA
- **Onde:** [AnimeGateway.kt:44-47](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/AnimeGateway.kt#L44-L47).
- **Problema:** `findAll(of((0..300).random(), size))` sorteia uma página entre 0 e 300. Se o catálogo tiver menos de `300 × size` animes, a maioria das páginas é **vazia** — as recomendações somem com frequência.
- **Impacto:** Bloco de recomendações intermitentemente vazio na home; pior quanto menor o catálogo.
- **Recomendação:** Amostragem real no Mongo com `{ $sample: { size: N } }` (via `MongoTemplate`/aggregation) — sorteio uniforme e independente do tamanho. Alternativa pobre: calcular `totalPages` e sortear dentro do range real.

#### Como corrigir (detalhado)
- **Abordagem A — `$sample` na aggregation (recomendada).** Uma query `Aggregation.newAggregation(sample(size))` devolve N documentos aleatórios em uma viagem, sem depender do total. *Prós:* correto e uniforme; *Contras:* `$sample` não usa índice (irrelevante em catálogo pequeno; ok aqui).
- **Abordagem B — calcular total e sortear página válida.** `val pages = ceil(count/size); of((0 until pages).random(), size)`. *Prós:* mínima mudança; *Contras:* um `count()` extra e ainda enviesa para páginas cheias.
- **✅ Recomendado:** **A** — mantém o `@Cacheable("recommendationsCache")` (TTL 2h já existente) e resolve de vez. Cuidar para o cache não "congelar" a mesma amostra além do desejado (o TTL de 2h já limita).

### BUG-4 · `getAllNames`: cast inseguro + `!!` + `catch` engolido 🐞 — ALTA
- **Onde:** [AnimeGateway.kt:65-87](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/AnimeGateway.kt#L65-L87).
- **Problema:** `it.sources!!.jikan!!` lança NPE se um anime não tiver fonte Jikan; `(title as HashMap<String, String>)` é um cast inseguro sobre `Any` da Jikan, dentro de um `try/catch(_)` que **engole** a exceção e refaz `(title as String)` — frágil dos dois lados. Modela títulos do MyAnimeList como `Any`.
- **Impacto:** Falha de indexação de nomes (a base do matching de `SearchUseCase`) silenciosa ou por NPE; nomes faltando = release não casa com anime.
- **Recomendação:** Tipar os títulos da Jikan com um modelo (`data class JikanTitle(val type: String?, val title: String?)`) em vez de `Any`/`HashMap`; guardar `sources?.jikan` com `?.let`/`continue`+log em vez de `!!`.

### BUG-5 · Contador de visitas com regex que não casa as rotas reais 🐞 — MÉDIA
- **Onde:** [RequestVisitCounterConfig.kt:21-39](src/main/kotlin/br/com/achimid/animesachimidv2/configurations/RequestVisitCounterConfig.kt#L21-L39).
- **Problema:** `PATHS` usa `Regex("/catalog")` — mas a rota de catálogo é `/animes` (ver `AnimesController`), então `/catalog` nunca casa. E `path.matches(it)` exige **match total**: `Regex("^/anime")` **não** casa `/anime/{slug}` (só o prefixo), logo páginas de anime individuais não contam. Resultado: o `pageAccess` exibido na home conta essencialmente só a raiz `/`.
- **Impacto:** Métrica de acessos exibida no site é incorreta/subestimada; decisões baseadas nela enganam.
- **Recomendação:** Trocar `matches` (full match) por `containsMatchIn`/`find`, ou usar âncoras corretas (`^/anime(/.*)?$`, `^/animes`). Confirmar os paths contra as rotas reais (`/`, `/animes`, `/anime/...`, `/calendar`).

### BUG-6 · `ReleaseGateway.fromDocument` cheio de `!!` 🐞 — MÉDIA
- **Onde:** [ReleaseGateway.kt:58-71](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/ReleaseGateway.kt#L58-L71).
- **Problema:** `document.anime?.source?.jikan?.url!!.split(...)` e `document.sources!!.map{...}` assumem que campos legados sempre existem. Um `ReleaseDocument` antigo (coleção `releases`) sem `sources` ou sem `anime.source.jikan.url` derruba a leitura com NPE — e isso ocorre **na renderização da home/listagem**, afetando o usuário final.
- **Impacto:** Uma única release "malformada" no banco quebra a página de listagem inteira (a stream `map` estoura).
- **Recomendação:** Usar `?.` + defaults (`?: emptyList()`, `?: ""`) em vez de `!!`; idealmente filtrar/logar documentos inconsistentes em vez de propagar. Liga com [TEST-1](#test-1--cobertura-zero) (teste com documento legado).

---

## B. Segurança 🔒

> Hoje o callback de scraping e a identidade por cookie são **abertos por design**. Os itens abaixo são pré-requisitos para qualquer endurecimento de produção, em ordem de exposição.

### SEC-1 · Callback de integração público e sem autenticação
- **Onde:** [SiteIntegrationAPIController.kt:31-35](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/SiteIntegrationAPIController.kt#L31-L35) — `POST /api/v1/site/integration/callback`.
- **Problema:** O endpoint que o serviço de Puppeteer chama de volta para entregar episódios raspados é **público, sem nenhum segredo**. Qualquer um pode `POST`ar um `CallbackIntegration` forjado e **criar releases arbitrários** (título, URL/mirror, anime, episódio). Combinado com [BUG-1.3](#bug-1--updatebyname-não-persiste-fora-da-memória--ignora-enabled--estoura-em-ref-desconhecido), um `ref` inexistente também derruba o processamento.
- **Impacto:** Injeção de conteúdo (releases falsos com links maliciosos exibidos aos usuários), poluição da base e da dedup, e um vetor de spam/abuso direto. A URL do mirror é exibida como link clicável no site.
- **Recomendação:** Segredo compartilhado entre o Puppeteer e a app — header `X-Callback-Token` comparado em tempo constante (`MessageDigest.isEqual`) a um `integration.callback-token` (env var), validado num `OncePerRequestFilter` só para `/api/v1/site/integration/callback`. Idealmente HMAC do corpo para também garantir integridade.

#### Como corrigir (detalhado)
- **Abordagem A — token estático em header (rápido).** O Puppeteer envia `X-Callback-Token: <segredo>`; um filtro dedicado rejeita com `401` se não bater. *Prós:* horas de trabalho, sem dependência nova; *Contras:* token único (sem identidade), precisa não vazar em logs.
- **Abordagem B — HMAC do payload.** O Puppeteer assina o corpo (`HMAC-SHA256` com segredo compartilhado) e envia em header; a app recomputa e compara. *Prós:* autentica **e** garante integridade (corpo não foi adulterado), resiste a replay se incluir timestamp/nonce; *Contras:* exige mudança coordenada no serviço de Puppeteer.
- **Abordagem C — mTLS / IP allowlist na borda.** Restringir por rede (reverse proxy). *Prós:* não toca a app; *Contras:* frágil se o Puppeteer for serverless/IP dinâmico; não protege se a rede for compartilhada.
- **✅ Recomendado:** **A agora** (trava a superfície em horas) com caminho para **B** quando der para coordenar a assinatura no Puppeteer. Em paralelo, validar o `ref` ([BUG-1.3](#bug-1--updatebyname-não-persiste-fora-da-memória--ignora-enabled--estoura-em-ref-desconhecido)) e validar o formato dos campos (URL `http(s)`, episódio numérico).
- **Passos:** 1) `integration.callback-token` em env/`application.yaml`; 2) `OncePerRequestFilter` no path do callback comparando em tempo constante; 3) configurar o Puppeteer para enviar o header; 4) `401` + métrica de rejeições; 5) evoluir para HMAC.

### SEC-2 · `POST /extraction/all/run` público
- **Onde:** [SiteIntegrationAPIController.kt:37-48](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/SiteIntegrationAPIController.kt#L37-L48).
- **Problema:** Dispara extração de **todas** as filas (FAST+MEDIUM+SLOW) sob demanda. Só há um `@RateLimiter` (1 req/10s) — que limita frequência, mas **não autentica**. Qualquer um aciona o scraping de dezenas de sites via serviço de Puppeteer.
- **Impacto:** DoS por amplificação (cada chamada explode em dezenas de jobs de navegador headless no serviço externo, que tem custo), e ruído/risco de IP-ban nos sites raspados.
- **Recomendação:** Proteger por auth admin (mesma barreira do [SEC-3](#sec-3--identidade-por-cookie-user_id-sem-assinatura)/futuro Spring Security) ou pelo mesmo token do [SEC-1](#sec-1--callback-de-integração-público-e-sem-autenticação). Manter o rate limiter como defesa em profundidade.

### SEC-3 · Identidade por cookie `user_id` sem assinatura; `isAdmin` confiável no cliente
- **Onde:** [RequestCookieConfig.kt:30-37](src/main/kotlin/br/com/achimid/animesachimidv2/configurations/RequestCookieConfig.kt#L30-L37), [AnimeAPIController.kt:29-45](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/AnimeAPIController.kt#L29-L45), [HomeController.kt:49](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/HomeController.kt#L49).
- **Problema:** O cookie `user_id` é um UUID **sem assinatura**, setado **sem `HttpOnly`, `Secure` nem `SameSite`** ([:32-34](src/main/kotlin/br/com/achimid/animesachimidv2/configurations/RequestCookieConfig.kt#L32)). Como é a única identidade, trocar o cookie no navegador **personifica qualquer usuário** (ver favoritos/comentar como outro). O `isAdmin` vem do `User` carregado por esse `user_id` — então qualquer rota que dependa de `isAdmin` é **trivialmente contornável** setando o cookie de um id admin (ou criando um user e marcando admin no Mongo, se houver caminho).
- **Impacto:** Sequestro de sessão anônima, ações em nome de terceiros, e (se houver gating por `isAdmin`) escalonamento de privilégio. Sem `HttpOnly`, um XSS ([SEC-4](#sec-4--thutext-na-sinopse--comentários-sem-sanitização)) lê o cookie direto.
- **Recomendação:** Curto prazo: setar `HttpOnly`+`Secure`+`SameSite=Lax` e **assinar** o cookie (HMAC) para detectar adulteração. Médio prazo: para qualquer ação sensível/admin, autenticação real (Spring Security + login), mantendo o cookie anônimo só para favoritos/preferências não-críticos.

#### Como corrigir (detalhado)
- **Atributos do cookie (fazer já, esforço P):** em [:32-34](src/main/kotlin/br/com/achimid/animesachimidv2/configurations/RequestCookieConfig.kt#L32), `cookie.isHttpOnly = true`, `cookie.secure = true` (atrás de HTTPS em prod), `SameSite=Lax` (via `ResponseCookie`/header). Reduz roubo via JS e CSRF básico.
- **Integridade da identidade — abordagens:**
  - **A — cookie assinado (HMAC).** Valor = `userId.assinatura`; a app recusa cookies adulterados. *Prós:* barato, detecta troca grosseira; *Contras:* não é autenticação — quem captura o cookie ainda personifica.
  - **B — Spring Security + login real para ações sensíveis.** Anônimo (cookie) só para favoritos; comentar/admin exige sessão autenticada. *Prós:* resolve personificação e o `isAdmin`; *Contras:* esforço M+, decisão de produto (introduzir login).
- **✅ Recomendado:** **Atributos do cookie + A imediatos**; **B** quando houver requisito de ação sensível/admin (anda junto com [SEC-2](#sec-2--post-extractionallrun-público) e [FEAT-2](#feat-2--painel-admin-para-sites-e-scripts)). Nunca tratar `isAdmin` lido do cookie como confiável para autorização real.

### SEC-4 · `th:utext` na sinopse + comentários sem sanitização (XSS)
- **Onde:** [anime.html:176](src/main/resources/templates/anime.html#L176) — `th:utext="${anime.getSynopsisTranslated()}"`; entrada de comentários via [AnimeAPIController.kt:20-27](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/AnimeAPIController.kt#L20-L27) → `AnimeComment.content`.
- **Problema:** `th:utext` renderiza HTML **sem escapar**. A sinopse PT-BR vem da tradução automática (LibreTranslate) a partir de texto da Jikan — se qualquer ponto da cadeia injetar HTML/`<script>`, ele executa no navegador. Comentários são aceitos via `@RequestBody` sem validação de tamanho/conteúdo nem sanitização explícita (depende de onde/como são renderizados — conferir o template que lista comentários).
- **Impacto:** Vetor de XSS armazenado (especialmente comentários, que são entrada direta do usuário). Combinado com [SEC-3](#sec-3--identidade-por-cookie-user_id-sem-assinatura) (cookie sem `HttpOnly`), um XSS rouba a identidade.
- **Recomendação:** Trocar `th:utext` por `th:text` na sinopse, a menos que se **precise** de HTML — e, se precisar, sanitizar com allowlist (ex.: OWASP Java HTML Sanitizer / Jsoup `clean`) antes de salvar/renderizar. Validar comentários (`@Size`, sanitização) e garantir que são renderizados com escape (`th:text`). Liga com [SEC-5](#sec-5--sem-headers-de-segurança--csp) (CSP como defesa em profundidade).

### SEC-5 · Sem headers de segurança / CSP
- **Onde:** Nenhuma configuração de headers no Spring (sem Spring Security; templates carregam Google Fonts, gtag, e JSON-LD inline).
- **Problema:** Não há `Content-Security-Policy`, `X-Content-Type-Options`, `Referrer-Policy`, `X-Frame-Options`/`frame-ancestors`. A única defesa contra XSS é o escape do Thymeleaf — e [SEC-4](#sec-4--thutext-na-sinopse--comentários-sem-sanitização) mostra um `th:utext` furando isso.
- **Impacto:** Defesa em profundidade ausente; um escape esquecido vira XSS explorável; sem `X-Frame-Options` há risco de clickjacking.
- **Recomendação:** Adicionar headers via `OncePerRequestFilter` (ou Spring Security `headers{}` se ele entrar). Os "baratos" (`nosniff`, `Referrer-Policy`, `frame-ancestors 'none'`, `Permissions-Policy`, HSTS em prod) independem de tudo. A CSP precisa de allowlist para `fonts.googleapis.com`/`fonts.gstatic.com`, `googletagmanager.com`/Google Analytics e os scripts inline (gtag + JSON-LD) — use **nonce** nos inline ou hashes. Internalizar fontes/Tailwind ([BUILD-1](#build-1--sem-pipeline-de-build-de-frontend)) permite uma CSP mais estrita.

### SEC-6 · Credenciais e operação (notas)
- **Onde:** [docker-compose.yaml](docker-compose.yaml) (`root`/`password`).
- **Nota:** Aceitável para dev local, mas documentar explicitamente que produção usa `MONGODB_URI`/`CALLBACK_URL` por env e que `.env`/segredos nunca vão ao repo (o `.gitignore` já deve cobrir). Sem severidade própria — fica como lembrete operacional.

---

## C. Pipeline e performance ⚡

### PIPE-1 · Dedup por `idt` com índice não-único e chave frágil
- **Onde:** [IntegrationEventDocument.kt:14-26](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/documents/IntegrationEventDocument.kt#L14-L26) (`idt` é `@Indexed`, **não** `unique`); [CallbackIntegration.kt](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/request/CallbackIntegration.kt) `getIdt()`; [SiteIntegrationGateway.kt:38-57](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/SiteIntegrationGateway.kt#L38-L57).
- **Problema:** A dedup faz `findByIdt` e, se nulo, `save` — **sem unicidade no banco**. Dois callbacks concorrentes do mesmo episódio (o job de minuto + a fila FAST podem coincidir) passam os dois pelo `findByIdt == null` e criam **dois eventos** → duas releases. Além disso, `getIdt()` concatena `title+anime+episode+from+url` removendo espaços: qualquer variação mínima na URL/título do site (querystring, tracking) gera um `idt` diferente e fura a dedup.
- **Impacto:** Releases duplicadas em corrida; dedup que não cobre variações benignas da fonte. O TTL de 3 dias (`@Indexed(expireAfter = "3d")`) é bom, mas não resolve a corrida.
- **Recomendação:** Tornar `idt` **único** (`@Indexed(unique = true)`) e tratar `DuplicateKeyException` como "já existe" (volta ao caminho `false` da dedup) — isso fecha a corrida atomicamente. Reavaliar a composição do `idt` para algo estável (ex.: `from + anime + episode`, normalizado), tirando a URL da chave se ela variar.

#### Como corrigir (detalhado)
- **Abordagem A — índice único + tratar `DuplicateKeyException`.** O `save` concorrente perde a corrida com exceção, que o gateway traduz para "duplicado" → não cria release. *Prós:* atômico, simples, usa o banco como árbitro; *Contras:* exige índice único limpo (cuidado se já houver duplicatas na coleção — limpar antes de criar o índice).
- **Abordagem B — upsert atômico (`findAndModify`/`upsert`).** Inserir só se não existir, retornando se foi criado. *Prós:* também atômico; *Contras:* mais código que A.
- **✅ Recomendado:** **A** + revisar a fórmula do `idt` para estabilidade. Validar no boot que o índice único existe. Liga com [BUG-2](#bug-2--createeventintegration-retorna-true-ao-falhar) (o tratamento de erro precisa devolver `false`, não `true`).

### PERF-1 · Sem índice composto `releases(animeId, episode)`
- **Onde:** [ReleaseRepository.kt:14-16](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/repositories/ReleaseRepository.kt#L14-L16) (`findByAnimeIdAndEpisode`, `findByAnimeIdOrderByEpisodeDesc`); chamada por release em [CreateReleaseUserCase.kt:26](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/CreateReleaseUserCase.kt#L26).
- **Problema:** `ReleaseDocument` não declara índice em `animeId`/`episode`. `CreateReleaseUserCase` faz `findByAnimeIdAndEpisodeNumber` **a cada release criada** (caminho quente do pipeline) — sem índice é **collection scan**, que piora linearmente com o crescimento de `releases`.
- **Impacto:** Latência crescente no pipeline e na página do anime (`findByAnimeIdOrderByEpisodeDesc`) conforme a base cresce; carga de CPU no Mongo.
- **Recomendação:** Adicionar índice composto `{ animeId: 1, episode: 1 }` (e `{ animeId: 1, episode: -1 }` para o `OrderByEpisodeDesc`, ou um só `{animeId, episode}` que serve ambos) via `@CompoundIndex` no `ReleaseDocument`. `auto-index-creation: true` já está ligado, então basta a anotação.

### PERF-2 · `supplyAsync` usa o ForkJoinPool comum, não as virtual threads
- **Onde:** [HomeController.kt:32-40](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/HomeController.kt#L32-L40) (7 `supplyAsync` paralelos) — padrão repetido nos demais `@Controller` de site.
- **Problema:** `CompletableFuture.supplyAsync(...)` sem `Executor` roda no **ForkJoinPool.commonPool** (dimensionado por nº de cores), não nas **virtual threads** que o projeto habilitou (`spring.threads.virtual.enabled: true`). Tarefas I/O-bound (Mongo, Jikan, SubsPlease) ocupam as poucas threads do common pool e podem serializar sob carga.
- **Impacto:** Sob concorrência, as buscas da home "paralelas" competem por um pool pequeno; o ganho das virtual threads não é aproveitado onde mais importa (fan-out de I/O).
- **Recomendação:** Passar um `Executor` de virtual threads (`Executors.newVirtualThreadPerTaskExecutor()`, exposto como bean) para os `supplyAsync`, ou migrar o fan-out para structured concurrency / `@Async` com executor virtual. Centralizar num helper para os controllers de site.

#### Como corrigir (detalhado)
- **Abordagem A — bean `Executor` virtual + `supplyAsync(task, executor)`.** Um `@Bean` `Executor` (virtual-thread-per-task) injetado nos controllers. *Prós:* mínima mudança, mantém o padrão atual, I/O escala bem; *Contras:* gerenciar o ciclo de vida do executor (Spring cuida se for bean).
- **Abordagem B — structured concurrency (`StructuredTaskScope`).** Mais idiomático em JDK recente, cancelamento/erro propagados. *Prós:* moderno, robusto; *Contras:* API e esforço maiores para o ganho atual.
- **✅ Recomendado:** **A** — barato e direto, aproveita as virtual threads já ligadas. Validar com um teste de carga simples na home (latência sob N requisições concorrentes) antes/depois.

### PERF-3 · Sem rate limit na Jikan + N+1 de chamadas no calendário
- **Onde:** [FindCalendarReleaseUseCase.kt:25-30](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/FindCalendarReleaseUseCase.kt#L25-L30) (busca por item do schedule → `searchUseCase.execute` por título), [SearchUseCase.kt:31](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/SearchUseCase.kt#L31) (`jikanAPIGateway.search`), [JikanAPIClient](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/http/jikan/JikanAPIClient.kt).
- **Problema:** A Jikan limita ~3 req/s e 60/min. O `FindCalendarReleaseUseCase` faz **uma busca por item** do schedule completo (N+1 de chamadas externas), e cada `SearchUseCase` que não casa localmente cai na Jikan. Não há `RateLimiter`/`Retry`/`Bulkhead` nos clientes Feign externos (o Resilience4j está no classpath e há `Resilience4jConfig` — **vazio**, ver [ARCH-5](#arch-5--código-morto-stub-vazio--resilience4jconfig-vazio--typo)).
- **Impacto:** Rajadas que tomam `429`/ban temporário da Jikan; calendário lento e sujeito a falhas parciais; sem retry, uma falha pontual perde o casamento daquele anime.
- **Recomendação:** Aplicar `@RateLimiter`/`@Retry` (Resilience4j) nos métodos do gateway Jikan respeitando ~3 req/s; cachear agressivamente buscas por título (já há `jikanIntegrationCache`/`animeSearchCache`); no calendário, evitar refazer a busca para títulos já resolvidos (persistir o vínculo título→animeId via `NameDocument`, que o `SearchUseCase.saveNameAsPossibility` já começa a fazer).

### PERF-4 · Estatísticas de cache coletadas mas não publicadas
- **Onde:** [CacheConfig.kt:42](src/main/kotlin/br/com/achimid/animesachimidv2/configurations/CacheConfig.kt#L42) (`recordStats()` ligado).
- **Problema:** O Caffeine já registra estatísticas, mas elas não são expostas (sem `CaffeineCacheMetrics` no Micrometer/Actuator). Não dá para ver hit ratio, evictions, tamanho — voa-se às cegas sobre o cache.
- **Impacto:** Impossível afinar TTL/tamanho dos 11 caches com base em dados (ex.: `recommendationsCache` de 5 entradas, `translateCache` de 1000).
- **Recomendação:** Registrar `CaffeineCacheMetrics.monitor(registry, cache, name)` para cada cache (ou usar o binder automático do Spring Cache + Micrometer) e expor via Actuator/Prometheus ([OBS-1](#obs-1--actuator-sem-exposição--métricas)). Esforço P.

---

## D. Arquitetura & qualidade de código 🧱

### ARCH-1 · `UseCase` vs `UserCase` (typo histórico)
- **Onde:** vários (`CreateReleaseUserCase`, `AddFavoriteUserCase`, `ProcessIntegrationCallbackUserCase`, `AfterCreateReleaseUserCase`, `RemoveFavoriteUserCase`, `TranslateAnimeInfoUserCase`, `AddCommentUserCase`) × os corretos (`SearchUseCase`, `FindAnimesUseCase`, `ExtractionTaskUseCase`, …).
- **Problema:** Metade dos casos de uso carrega o typo `UserCase`. O próprio `CLAUDE.md` registra a inconsistência. Ruído cognitivo e quebra de convenção.
- **Impacto:** Baixo funcionalmente, mas atrapalha navegação, autocompletar e a coerência do código novo.
- **Recomendação:** Rename em massa para `UseCase` (refatoração segura da IDE — renomeia classe + arquivo + referências). Fazer num commit isolado, sem outras mudanças, para revisão trivial. Esforço M só pelo volume de arquivos tocados.

### ARCH-2 · Lombok num projeto 100% Kotlin
- **Onde:** [build.gradle:57,63](build.gradle) (`compileOnly`/`annotationProcessor 'org.projectlombok:lombok'`).
- **Problema:** Lombok não tem função num projeto Kotlin (`data class`, `val/var`, default args cobrem tudo). Mantém um annotation processor e o `configurations.compileOnly.extendsFrom(annotationProcessor)` por causa dele.
- **Impacto:** Dependência morta; tempo de build e superfície de configuração desnecessários.
- **Recomendação:** Remover ambas as linhas do Lombok e, se nada mais usar, o bloco `configurations { compileOnly { extendsFrom annotationProcessor } }`. Confirmar que nenhum `.java` com Lombok existe (o projeto é Kotlin). Esforço P.

### ARCH-3 · Vazamento de camada: DTO de request HTTP até a persistência
- **Onde:** `CallbackIntegrationExecutionResult` (em `gateways/inputs/http/api/request/`) é importado por `usecases` ([CreateReleaseUserCase.kt:5](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/CreateReleaseUserCase.kt#L5), [ProcessIntegrationCallbackUserCase.kt:4](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/ProcessIntegrationCallbackUserCase.kt#L4)) e por `gateways/outputs/mongodb` ([SiteIntegrationGateway.kt:4](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/SiteIntegrationGateway.kt#L4)).
- **Problema:** Em Clean Architecture, o DTO de entrada HTTP deveria ser convertido para um modelo de **domínio** na borda; aqui ele trafega até a persistência. O domínio fica acoplado ao formato do request (e o `getIdt()` — regra de dedup — mora num DTO de request, não no domínio).
- **Impacto:** Mudar o contrato HTTP do callback impacta use cases e gateways; lógica de negócio (dedup) escondida num DTO de borda; dificulta testar o domínio isolado.
- **Recomendação:** Criar um domínio "evento raspado" (ex.: `ScrapedEvent`) com a regra de identidade (`idt`) como método de domínio; o controller mapeia `CallbackIntegration` → `ScrapedEvent` na entrada. Use cases e gateways passam a depender só do domínio. Liga com [ARCH-6](#arch-6--mutabilidade-nos-domínios) e [TEST-1](#test-1--cobertura-zero).

### ARCH-4 · `GlobalExceptionHandler` vazio + `catch (RuntimeException)` amplo
- **Onde:** [GlobalExceptionHandler.kt](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/exception/GlobalExceptionHandler.kt) (arquivo praticamente vazio — sem `@ExceptionHandler`); `catch (ex: RuntimeException)` em [ProcessIntegrationCallbackUserCase.kt:32](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/ProcessIntegrationCallbackUserCase.kt#L32), [FindCalendarReleaseUseCase.kt:33](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/FindCalendarReleaseUseCase.kt#L33), `catch (e: Exception)` em [PuppeteerAPIGateway.kt:24](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/http/PuppeteerAPIGateway.kt#L24).
- **Problema:** Sem tratamento central, exceções da API saem como `500` genérico do Spring (vazando stacktrace conforme config). Os `catch` amplos engolem/relançam tudo igual, sem distinguir erro transitório (timeout externo) de erro de programação. Existe `AnimeNotFoundException` no domínio, mas não há handler que a traduza em `404`.
- **Impacto:** Respostas de erro inconsistentes; `AnimeNotFoundException` provavelmente vira `500` em vez de `404`; difícil observar/alarmar por tipo de erro.
- **Recomendação:** Popular o `@RestControllerAdvice` com `@ExceptionHandler` para `AnimeNotFoundException` → `404`, validação (`MethodArgumentNotValidException`) → `400`, e um handler genérico `500` que loga e devolve um corpo de erro estável (sem stacktrace). Definir exceções de domínio específicas e parar de capturar `RuntimeException`/`Exception` cru onde dá para tratar tipos concretos.

### ARCH-5 · Código morto: stub vazio + `Resilience4jConfig` vazio + typo `createEvenIntegration`
- **Onde:** [ProcessSiteIntegrationCallbackUseCase.kt](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/ProcessSiteIntegrationCallbackUseCase.kt) (stub vazio, confunde com o real `ProcessIntegrationCallbackUserCase`); [Resilience4jConfig.kt](src/main/kotlin/br/com/achimid/animesachimidv2/configurations/Resilience4jConfig.kt) (**arquivo vazio**, sem conteúdo); método `createEvenIntegration` ([SiteIntegrationGateway.kt:38](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/SiteIntegrationGateway.kt#L38)) com typo (falta o "t").
- **Problema:** Artefatos mortos/inconsistentes acumulam ruído. O `Resilience4jConfig` vazio sugere intenção não concluída de configurar resilience (ver [PERF-3](#perf-3--sem-rate-limit-na-jikan--n1-de-chamadas-no-calendário)).
- **Impacto:** Confusão (qual é o callback "real"?), risco de alguém editar o stub errado, e sinal de feature pela metade.
- **Recomendação:** Remover o stub vazio e o `Resilience4jConfig` vazio (ou preenchê-lo com a config de rate limit do [PERF-3](#perf-3--sem-rate-limit-na-jikan--n1-de-chamadas-no-calendário)); corrigir o typo `createEvenIntegration` → `createEventIntegration` (rename da IDE). Esforço P.

### ARCH-6 · Mutabilidade nos domínios
- **Onde:** [AnimeDocument.kt:31,33](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/documents/AnimeDocument.kt#L31) (`var descriptionPtBr`/`var synopsisPtBr`); [Release.kt:13](src/main/kotlin/br/com/achimid/animesachimidv2/domains/Release.kt#L13) (`options: MutableList`); [SiteIntegration.kt:19-21](src/main/kotlin/br/com/achimid/animesachimidv2/domains/SiteIntegration.kt#L19) (`var lastExecution*`); mutação in-place em [TranslateAnimeInfoUserCase.kt:31-32](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/TranslateAnimeInfoUserCase.kt#L31) e [CreateReleaseUserCase.kt:40-42](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/CreateReleaseUserCase.kt#L40).
- **Problema:** Domínios mutáveis convidam a efeitos colaterais (o `TranslateAnimeInfoUserCase` muta o `Anime` recebido; o `CreateReleaseUserCase` adiciona em `options` mutável). Em código concorrente (virtual threads, `@Async`) isso é arriscado.
- **Impacto:** Bugs sutis de estado compartilhado; dificulta raciocinar sobre o fluxo assíncrono (`AfterCreateReleaseUserCase` é `@Async`).
- **Recomendação:** Preferir imutabilidade + `copy()` (`anime.copy(synopsisPtBr = ...)`) e listas imutáveis com reconstrução. Onde a mutação for de fato necessária (estado de execução das integrações), isolar e documentar. Liga com [ARCH-3](#arch-3--vazamento-de-camada-dto-de-request-http-até-a-persistência).

### ARCH-7 · `Anime` × `AnimeDocument` quase idênticos e extensos (nota)
- **Onde:** [AnimeDocument.kt](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/documents/AnimeDocument.kt) × `domains/Anime.kt` (≈20 campos espelhados; MapStruct faz a ponte).
- **Nota:** A separação domínio/documento é correta em Clean Architecture, mas o custo de manter ~20 campos duplicados + mapper é alto para o ganho neste projeto. Não é um bug — vale **revisar** se a fronteira agrega valor ou se um modelo único reduziria atrito (decisão de trade-off, não ação imediata). Sem severidade.

---

## E. SEO 🔎

> O on-page já é decente (`lang="pt-br"`, `<title>`/`description`/`canonical`/OpenGraph por página, JSON-LD `TVSeries`, `sitemap.xml`, `robots.txt`, GA + Search Console verificados). Os itens abaixo são lacunas pontuais.

### SEO-1 · `sitemap.xml` sem `<lastmod>` e com prioridades estáticas
- **Onde:** [sitemap.html](src/main/resources/templates/sitemap.html) + [SitemapController.kt:16-26](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/SitemapController.kt#L16-L26).
- **Problema:** O sitemap lista as URLs (`/`, `/animes`, `/calendar` + `/anime/{slug}` de todos os slugs) com `changefreq`/`priority` fixos e **sem `<lastmod>`**. Crawlers usam `lastmod` para decidir reindexação; sem ele, páginas que mudaram (novo episódio) não sinalizam atualização.
- **Impacto:** Reindexação mais lenta das páginas de anime que receberam releases novos — justamente o conteúdo que muda.
- **Recomendação:** Incluir `<lastmod>` por anime = data do último release/episódio (ou `updatedAt` do `AnimeDocument`). O `FindAllSlugsUseCase` passaria a devolver `(slug, lastmod)`. Avaliar mover a geração do sitemap para `produces=application/xml` real (já está) e cacheá-lo. Esforço P–M (depende de carregar o lastmod junto dos slugs).

### SEO-2 · Domínio canônico hardcoded nos templates
- **Onde:** [anime.html:21](src/main/resources/templates/anime.html#L21), [home.html:32](src/main/resources/templates/home.html#L32), [sitemap.html:5,11](src/main/resources/templates/sitemap.html#L5), [robots.txt](src/main/resources/static/robots.txt) — todos com `https://animes.achimid.com.br` literal.
- **Problema:** O domínio canônico está repetido como string em vários templates. Trocar de domínio (ou ter ambiente de staging com canonical correto) exige editar N arquivos; risco de divergência (canonical de staging apontando para prod, gerando conteúdo duplicado aos olhos do Google).
- **Impacto:** Manutenção frágil; risco de canonical errado em ambientes não-prod.
- **Recomendação:** Centralizar numa config `app.base-url` (`@ConfigurationProperties`/`@Value`) exposta como atributo global do modelo (`@ControllerAdvice` com `@ModelAttribute`), e usar `${baseUrl}` nos templates e no sitemap/robots. Esforço P.

### SEO-3 · JSON-LD com `ratingCount: 10000` fixo
- **Onde:** [anime.html:54-56](src/main/resources/templates/anime.html#L54) — `"aggregateRating": { "ratingValue": [[${anime.score}]], "ratingCount": 10000 }`.
- **Problema:** O `ratingCount` (e `bestRating/worstRating`) é **hardcoded em 10000** para todo anime, com `ratingValue` vindo do score da Jikan. É um dado estruturado **factualmente falso** entregue ao Google.
- **Impacto:** Risco de penalização por *structured data* enganoso / rich result removido; credibilidade. O Google audita `aggregateRating` inventado.
- **Recomendação:** Ou usar contagem real (não temos avaliações próprias — então provavelmente **remover** `aggregateRating`, mantendo só `name`/`image`/`genre`/`description`), ou, se quiser refletir a Jikan, usar os números reais da Jikan (`scored_by`) e atribuir a fonte. O mais seguro: **remover o `aggregateRating` fabricado**.

### SEO-4 · Core Web Vitals: dimensões de imagem e fonte sem `display=swap` na página de anime
- **Onde:** [anime.html:28](src/main/resources/templates/anime.html#L28) (fonte sem `&display=swap`, ao contrário da home); capas em `animes.html`/`calendar.html`.
- **Problema:** A **home já acerta** o essencial (capas com `th:alt`, `loading="lazy"` e `fetchpriority="high"` na LCP — [home.html:77-164](src/main/resources/templates/home.html#L77)). Lacunas restantes: as capas não declaram `width`/`height` (nem `aspect-ratio`), o que gera **CLS** ao carregar; e a fonte do `anime.html` **não** usa `display=swap` (a home usa), causando FOIT/atraso de texto. Confirmar `loading="lazy"` em `animes.html`/`calendar.html`.
- **Impacto:** LCP/CLS piores, especialmente no mobile e em listas grandes; penaliza ranking (CWV são fator de rank).
- **Recomendação:** Adicionar `loading="lazy"` + `width`/`height` (ou `aspect-ratio` CSS) nas capas; `&display=swap` na fonte do `anime.html`; `fetchpriority="high"` + preload na capa principal (LCP) da página de anime. Liga com [BUILD-1](#build-1--sem-pipeline-de-build-de-frontend) (otimização de assets). Esforço M.

---

## F. Design & UX 🎨

### UX-1 · Tema fixo escuro sem `prefers-color-scheme` e manifest inconsistente
- **Onde:** CSS em `static/css/` (variável `--bg-dark`, sem media query `prefers-color-scheme`); [site.webmanifest](src/main/resources/static/favicon/site.webmanifest) (`theme_color`/`background_color` = `#ffffff`).
- **Problema:** O site é escuro por padrão (adequado ao público), mas não responde a `prefers-color-scheme` (sem opção de tema claro) e o `theme_color`/`background_color` do manifest é **branco** — inconsistente com a UI escura (a barra do navegador/splash do PWA fica branca).
- **Impacto:** Flash branco no PWA/splash; sem escolha de tema. Baixa prioridade (dark é uma escolha de produto válida).
- **Recomendação:** No mínimo, alinhar `theme_color`/`background_color` do manifest ao tema escuro real. Se quiser tema claro, introduzir variáveis CSS por `prefers-color-scheme` + toggle persistido. Esforço M (toggle) / P (só o manifest).

### UX-2 · Estados de carregamento das listas assíncronas
- **Onde:** listas que dependem de chamadas externas (calendário/SubsPlease, busca) e o fan-out da home ([HomeController.kt:32-40](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/HomeController.kt#L32)).
- **Problema:** Como a home agrega 7 fontes em paralelo e o calendário depende de API externa, há janelas de espera. Há referências a "loading" em alguns CSS/templates, mas vale garantir **skeletons** consistentes (placeholders dimensionados) em vez de áreas vazias que saltam (CLS) quando os dados chegam.
- **Impacto:** Percepção de lentidão e layout shift quando uma das fontes demora (ex.: SubsPlease lento).
- **Recomendação:** Skeletons dimensionados nas seções assíncronas; como o SSR já bloqueia até `allOf().join()`, considerar renderizar o essencial primeiro e hidratar o resto, ou ao menos placeholders que reservem espaço. Esforço M.

### UX-3 · Acessibilidade
- **Onde:** templates em geral — **nenhum `aria-label`** encontrado; conferir contraste no tema escuro e navegação por teclado.
- **Problema:** Sem `aria-label` em controles só-ícone (busca, navegação, botões de favoritar), foco de teclado não garantido, e contraste de texto sobre imagens de fundo (`.anime-hero` com gradiente) pode falhar WCAG AA. As capas já têm `alt` (bom).
- **Impacto:** Barreira para usuários de leitor de tela/teclado; risco legal/SEO (acessibilidade é sinal indireto).
- **Recomendação:** `aria-label` nos controles só-ícone; `:focus-visible` visível; checar contraste (AA) com ferramenta; garantir que modais/tooltips sejam operáveis por teclado e tenham `role` adequado. Esforço M.

### UX-4 · PWA incompleto (manifest com bugs, sem service worker)
- **Onde:** [site.webmanifest](src/main/resources/static/favicon/site.webmanifest); nenhum service worker no projeto.
- **Problema:** O manifest tem `name`/`short_name` **vazios** (`""`) e os `icons[].src` apontam para `/android-chrome-192x192.png` — mas os arquivos estão em **`/favicon/`** (`/favicon/android-chrome-192x192.png`). Ou seja, os ícones do PWA **quebram** (404) e o app instalável fica sem nome. Não há service worker, então não há offline/instalação real.
- **Impacto:** "Adicionar à tela inicial" gera um app sem nome e sem ícone; nenhum benefício offline.
- **Recomendação:** Corrigir `name`/`short_name` e os caminhos dos ícones (`/favicon/...`) — esforço P, é praticamente um bug. Depois, se quiser PWA de verdade, adicionar um service worker mínimo (cache de assets estáticos + fallback offline) — esforço M, casa com [BUILD-1](#build-1--sem-pipeline-de-build-de-frontend).

---

## G. Build, testes e observabilidade

### BUILD-1 · Sem pipeline de build de frontend
- **Onde:** não há `package.json`/bundler; `static/css` e `static/js` são servidos crus (5 CSS + 5 JS não minificados); fontes via Google CDN.
- **Problema:** Os assets vão à rede sem minificação/bundle e **sem hash no nome** (cache-busting). O `Cache-Control` de 30 dias em prod é ótimo para performance, mas **perigoso sem hash**: ao publicar um CSS/JS novo, navegadores podem servir a versão velha por até 30 dias.
- **Impacto:** Mais bytes que o necessário; e, sobretudo, **risco de servir asset desatualizado** após deploy (o cache longo + nome fixo = usuários presos no CSS/JS antigo).
- **Recomendação:** Introduzir um build leve (esbuild para JS, minificador de CSS) que produza arquivos com **hash no nome** + um esquema de referência versionada nos templates. Como o projeto é Gradle-first, integrar via plugin `node-gradle` (build self-contained no CI) ou um passo simples. Internalizar fontes (e Tailwind/Alpine se vierem a ser usados) habilita CSP estrita ([SEC-5](#sec-5--sem-headers-de-segurança--csp)). Esforço M.

#### Como corrigir (detalhado)
- **Abordagem A — esbuild + minify CSS via task Gradle (`node-gradle`).** O Gradle baixa Node e roda o bundle antes de `processResources`. *Prós:* CI não precisa de Node pré-instalado; build reprodutível; *Contras:* um plugin a mais.
- **Abordagem B — scripts npm chamados por `Exec` do Gradle.** *Prós:* mais simples/transparente; *Contras:* exige Node no ambiente de build.
- **✅ Recomendado:** **A** pela reprodutibilidade no CI. O ganho mais urgente não é o tamanho, é o **cache-busting com hash** — sem ele o `max-age` de 30 dias é um risco. Manter simples (sem SPA): é um site Thymeleaf.
- **Passos:** 1) bundler + hash no nome; 2) referência versionada nos templates (manifest de assets ou `th:href` derivado); 3) internalizar fontes; 4) integrar à `bootJar`; 5) validar que um deploy invalida o cache do cliente.

### BUILD-2 · CI só builda a imagem; sem testes nem lint
- **Onde:** [.github/workflows/docker-ci.yml](.github/workflows/docker-ci.yml) — builda e publica a imagem; o `Dockerfile` roda `bootJar -x test` (pula testes).
- **Problema:** Nenhum job roda `./gradlew test` nem lint. Como o `Dockerfile` usa `-x test`, mesmo se houvesse testes eles **não** rodariam no pipeline. Sem `detekt`/`ktlint`, o estilo Kotlin não é verificado.
- **Impacto:** Regressões e quebras de estilo passam direto; a imagem publicada pode conter código que nem compila os testes.
- **Recomendação:** Adicionar um job que roda `./gradlew test` (e `detekt`/`ktlint`) **antes** do build da imagem, com cache de Gradle. Liga com [TEST-1](#test-1--cobertura-zero). Esforço P (o workflow base já existe).

### TEST-1 · Cobertura ~zero
- **Onde:** `src/test` só tem `contextLoads()`. As dependências de teste (`spring-boot-starter-*-test`, `kotlin-test-junit5`) já estão no [build.gradle:66-71](build.gradle).
- **Problema:** Nenhuma rede de segurança. O `CLAUDE.md` exige validar manualmente cada mudança justamente porque nada é coberto. O coração do produto (matching fuzzy, agrupamento de mirrors, dedup) é frágil e não testado.
- **Impacto:** Refatorar (qualquer item deste backlog) é arriscado; bugs como **BUG-1..6** existem sem nada que os pegue.
- **Recomendação:** Priorizar por valor, não cobertura total. Começar pelos **invariantes que mais doem**:
  - `SearchUseCase` (proximidade → fuzzy ≥97 → Jikan → fuzzy ≥95 → fallback) — lógica central, com gateways mockados (MockK).
  - `CreateReleaseUserCase` (agrupar mirrors no mesmo `Release`, não duplicar `option` por `from`).
  - Dedup (`createEventIntegration`) — incluindo o caso de erro do [BUG-2](#bug-2--createeventintegration-retorna-true-ao-falhar).
  - Mappers MapStruct (`merge(anime, jikan)`).
  - Parsing/`padLeft`/`unpadLeft` e a leitura legada do [BUG-6](#bug-6--releasegatewayfromdocument-cheio-de-).
  - `@DataMongoTest`/Testcontainers para os gateways e os **índices** ([PERF-1](#perf-1--sem-índice-composto-releasesanimeid-episode), [PIPE-1](#pipe-1--dedup-por-idt-com-índice-não-único-e-chave-frágil)).
- **Adotar a regra:** toda correção deste backlog entra com um teste que falharia sem ela. Esforço G (contínuo).

### OBS-1 · Actuator sem exposição / métricas
- **Onde:** [build.gradle:38](build.gradle) (`spring-boot-starter-actuator` presente); sem config de `management.endpoints` no `application.yaml`; sem registry Prometheus.
- **Problema:** O Actuator está no classpath mas não há exposição configurada nem `micrometer-registry-prometheus`; o `recordStats()` do cache ([PERF-4](#perf-4--estatísticas-de-cache-coletadas-mas-não-publicadas)) não vira métrica. Não há visibilidade de health/latência/cache.
- **Impacto:** Operação às cegas: sem hit ratio de cache, sem latência de tradução/Jikan, sem readiness real.
- **Recomendação:** Expor `health,info,metrics,prometheus` (restrito em prod), adicionar `micrometer-registry-prometheus`, ligar `CaffeineCacheMetrics` e timers nas chamadas externas caras (tradução, Jikan, Puppeteer). Esforço P.

### OBS-2 · Sem healthcheck no Docker / readiness-liveness
- **Onde:** [Dockerfile](Dockerfile) e [docker-compose.yaml](docker-compose.yaml) — sem `HEALTHCHECK`; sem probes.
- **Problema:** Orquestradores não sabem quando a app está pronta (a app tem `shutdown: graceful`, mas nada checa readiness/liveness). O Mongo do compose também não tem healthcheck (a app pode subir antes do banco).
- **Impacto:** Deploys que rotacionam tráfego para instâncias não-prontas; possível corrida app↔Mongo no boot local.
- **Recomendação:** Após [OBS-1](#obs-1--actuator-sem-exposição--métricas), usar `actuator/health/readiness` e `liveness` como probes; `HEALTHCHECK` no Dockerfile/compose; `depends_on` com `condition: service_healthy` no compose. Esforço P.

### OBS-3 · Logs estruturados + correlação (nota)
- **Nota:** O fluxo é assíncrono (`runAsync` no callback, `@Async` na tradução), o que torna difícil seguir uma requisição nos logs. Considerar logs em JSON + um `traceId`/MDC propagado pelo callback → release → tradução. Liga com OBS-1. Sem severidade (melhoria de diagnóstico).

---

## H. Funcionalidades (roadmap de produto)

### FEAT-1 · `languages`/`isDub` chegam no callback mas não são persistidos
- **Onde:** [CallbackIntegration.kt:26-27](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/request/CallbackIntegration.kt#L26) (`languages`, `isDub` no DTO) × [CreateReleaseUserCase.kt:27-36](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/CreateReleaseUserCase.kt#L27) e [Release.kt](src/main/kotlin/br/com/achimid/animesachimidv2/domains/Release.kt) (não têm os campos).
- **Problema:** O scraping já captura idioma/dub-vs-leg, mas o `CreateReleaseUserCase` ignora — `Release`/`ReleaseDocument` não têm esses campos. A informação se perde.
- **Impacto:** Não dá para exibir badges "Dublado"/"Legendado"/idioma nos cards, nem filtrar por isso — informação útil que já está sendo coletada vai pro lixo.
- **Recomendação:** Propagar `languages`/`isDub` até `Release`/`ReleaseDocument` (por mirror/option, já que diferentes fontes podem diferir) e exibir badges no card. Esforço M.

### FEAT-2 · Painel admin para sites e scripts
- **Contexto:** Como o [SiteIntegrationRepository](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/repositories/SiteIntegrationRepository.kt#L9-L37) é hardcoded ([BUG-1](#bug-1--updatebyname-não-persiste-fora-da-memória--ignora-enabled--estoura-em-ref-desconhecido)), habilitar/desabilitar um site ou editar um script exige **editar código + redeploy**. Um painel admin (após migrar para Mongo) permitiria ligar/desligar sites, editar scripts, ver status real (último sucesso por site, throughput) e disparar extração manual — sem mexer no banco na mão. Depende de auth admin real ([SEC-2](#sec-2--post-extractionallrun-público)/[SEC-3](#sec-3--identidade-por-cookie-user_id-sem-assinatura)) e da persistência ([BUG-1](#bug-1--updatebyname-não-persiste-fora-da-memória--ignora-enabled--estoura-em-ref-desconhecido) abordagem A). Esforço G.

### FEAT-3 · Notificações de lançamento para favoritos
- **Contexto:** A base de favoritos por usuário já existe (`User.favorites`). Quando um `Release` é criado para um anime favoritado, dá para notificar (Web Push / e-mail). É o tipo de feature que aumenta retenção e aproveita dados já existentes. Requer captar consentimento/endpoint de push e um disparo no `AfterCreateReleaseUserCase`. Esforço G.

### FEAT-4 · Filtros no catálogo
- **Contexto:** O catálogo (`/animes`) poderia filtrar por tipo (TV/OVA/Movie), status (em exibição/finalizado), gênero/tags e nota — campos que o `AnimeDocument` já tem (`type`, `status`, `tags`, `score`). Melhora descoberta. Exige queries paginadas com filtros (e índices correspondentes). Esforço M.

### FEAT-5 · Qualidade de tradução e alinhamento do README (nota)
- **Contexto:** O README cita um badge do Google Gemini, mas a tradução em uso é **LibreTranslate** (confirmado no código). Alinhar a documentação e, separadamente, **medir a qualidade** das sinopses traduzidas — LibreTranslate costuma ser inferior a alternativas; se a qualidade incomodar, avaliar provedor melhor (com cache, já existe `translateCache`). Liga com [SEC-4](#sec-4--thutext-na-sinopse--comentários-sem-sanitização) (a saída da tradução é renderizada com `th:utext`).

---

## Sequência sugerida (alto valor / baixo custo primeiro)

1. **Bugs baratos e de alto impacto:** BUG-2, BUG-3, BUG-1 (itens 2 e 3), BUG-5, BUG-6, UX-4 (caminhos do manifest) — todos `P`, corrigem comportamento errado já em produção.
2. **Trancar a superfície aberta:** SEC-1 (token no callback) → SEC-2 → SEC-3 (atributos+assinatura do cookie) → SEC-4 (`th:utext`/sanitização). Pré-requisitos de exposição pública.
3. **Robustez do pipeline:** PIPE-1 (índice único da dedup, junto com BUG-2) e PERF-1 (índice composto de releases).
4. **Performance barata:** PERF-2 (executor virtual), PERF-4 + OBS-1 (visibilidade de cache/métricas), PERF-3 (rate limit Jikan — preenchendo o `Resilience4jConfig` vazio do ARCH-5).
5. **SEO barato:** SEO-3 (remover rating fabricado), SEO-2 (domínio único), SEO-1 (`lastmod`), SEO-4.
6. **Higiene de código:** ARCH-5 (código morto), ARCH-2 (Lombok), ARCH-4 (handler de exceções), depois ARCH-1 (rename em massa) e ARCH-3/ARCH-6 (refactor de fronteira/imutabilidade).
7. **Esforço maior, alto valor:** TEST-1 (testes — habilita tudo com segurança), BUILD-1/BUILD-2 (build+CI), e as FEATs (admin, notificações, filtros).

> _Documento gerado por análise estática + leitura dirigida do código. As severidades refletem o estado atual; várias sobem de prioridade conforme o volume de dados e a base de usuários crescerem. Ao concluir um item, mova-o para um CHANGELOG ou marque como ✅ com a rodada/PR correspondente._
