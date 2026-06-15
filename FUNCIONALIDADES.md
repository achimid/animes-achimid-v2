# Funcionalidades & Design — Roadmap de Produto

Este documento reúne **sugestões de novas funcionalidades** e **melhorias de design/UX** para o
**Animes Achimid v2**, com análise detalhada de implementação. Diferente de
[MELHORIAS.md](MELHORIAS.md) (dívida técnica: bugs, segurança, performance, arquitetura), aqui o
foco é **evolução de produto**. Quando um item depende de uma dívida já catalogada, este doc apenas
**referencia** o `MELHORIAS.md` em vez de repetir.

> A lista crua de ideias que originou este doc está em [proximosprompts.md](proximosprompts.md).
> Aqui ela vira análise acionável, com arquivos reais a tocar e dependências entre itens.

## Como ler este documento

- **Seção 1 — Fundações transversais** (`F1`–`F3`): pré-requisitos que destravam vários itens.
- **Seção 2 — Funcionalidades** (`FUNC-01`–`FUNC-14`): o núcleo, um bloco por ideia da lista.
- **Seção 3 — Design**: como polir a identidade visual atual (dark + neon) sem rompê-la.
- **Seção 4 — Roadmap**: sequenciamento sugerido respeitando dependências.

Cada item segue o formato: **Problema/Objetivo → Como implementar → Arquivos → Esforço · Prioridade · Depende de**.

**Legenda de esforço:** 🟢 P (pequeno, ~1 dia) · 🟡 M (médio, alguns dias) · 🔴 G (grande, 1+ semana).
**Prioridade:** ⭐ alta · ◐ média · ○ baixa.

## ✅ Já implementadas

| ID | Funcionalidade | Data |
|----|----------------|------|
| FUNC-01 | Corrigir o botão "Exibir mais" da home | 2026-06-13 |
| F3 | Design tokens semânticos | 2026-06-13 |
| FUNC-11 | Páginas legais + consentimento de cookies | 2026-06-13 |
| F1 | Autenticação Google (OIDC) + anônimo + merge | 2026-06-13 |
| FUNC-06 | Login Google + anônimo (UX no header) | 2026-06-13 |
| FUNC-03 | Página do usuário | 2026-06-13 |
| FUNC-09 | Detalhe bilíngue (PT + EN) | 2026-06-13 |
| FUNC-02 | Favoritos: página dedicada + endpoint + estado | 2026-06-13 |
| F2 | `SiteIntegration` no MongoDB | 2026-06-13 |
| FUNC-05 | Revisão/aprovação de comentários | 2026-06-13 |
| FUNC-04 | Área admin | 2026-06-13 |
| FUNC-14 | Mais sites monitorados | 2026-06-13 |
| FUNC-08 | Agenda híbrida (cobrir ingeridos) | 2026-06-13 |
| FUNC-10 | Recomendações inteligentes | 2026-06-13 |
| FUNC-13 | Mais APIs de enriquecimento (AniList) | 2026-06-13 |
| FUNC-07 | Notificações de favoritos lançados (in-app) | 2026-06-13 |
| FUNC-07b | Web Push: permissão ao favoritar + assinatura VAPID | 2026-06-14 |
| FUNC-15 | Preferência de site de notificação por anime | 2026-06-14 |
| FUNC-16 | Transferência de identidade anônima entre dispositivos | 2026-06-14 |
| FUNC-17 | Gerenciamento centralizado de favoritos em `/favoritos` | 2026-06-14 |
| BUG-CAL | Correção do calendário (índice de dia incorreito no domingo) | 2026-06-14 |

**FUNC-01** — **causa-raiz** identificada no smoke test: o **Spring Boot 4 mudou a serialização de
`Page`** para `{ content, page: { number, totalPages, ... } }`, então o `result.number` que o JS lia
virava `undefined`/`NaN` e quebrava a paginação a partir do 2º clique. Em
[home.js](src/main/resources/static/js/home.js): paginação corrigida para `result.page.number` /
`result.page.totalPages` com detecção de fim (botão desabilita e mostra "Você chegou ao fim"), trava
de cliques concorrentes (`isLoadingReleases`), `pageSize` explícito (20), tratamento de erro com
retry e correção da corrida no `autoReload`. Corrigido também o `href` sem aspas do botão "play" no
JS e no template [home.html](src/main/resources/templates/home.html).

**F3** — em [common.css](src/main/resources/static/css/common.css): nova camada de **tokens
semânticos** (`--color-*`, `--space-*`, `--radius-*`, sombras e tipografia) descrevendo papéis, com
valores idênticos aos atuais (zero mudança visual). Primitivos mantidos para os demais CSS, e o
próprio `common.css` já migrado para os tokens semânticos. Base para o tema claro (D1).

**FUNC-11** — novo [LegalController](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/LegalController.kt)
servindo `/dmca`, `/cookies`, `/privacidade` e `/termos` pelo template
[legal.html](src/main/resources/templates/legal.html); links adicionados ao rodapé
([fragments/layout.html](src/main/resources/templates/fragments/layout.html) e ao footer próprio do
anime.html). Banner de consentimento de cookies injetado em todas as páginas via
[common.js](src/main/resources/static/js/common.js), com escolha persistida em `localStorage` e
**Google Consent Mode** (Analytics negado por padrão, ativado só após o aceite) nos `<head>` das
páginas. Estilos em [common.css](src/main/resources/static/css/common.css).

**F1** — Spring Security + OAuth2 client adicionados ([build.gradle](build.gradle)).
[SecurityConfig](src/main/kotlin/br/com/achimid/animesachimidv2/configurations/SecurityConfig.kt)
mantém site e API públicos (inclusive o callback), CSRF desabilitado (preserva os `fetch` atuais) e
liga o login Google **só** quando há credenciais (`ObjectProvider<ClientRegistrationRepository>`).
[OAuthLoginSuccessHandler](src/main/kotlin/br/com/achimid/animesachimidv2/configurations/OAuthLoginSuccessHandler.kt)
+ [LoginWithGoogleUseCase](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/LoginWithGoogleUseCase.kt)
resolvem/mesclam a conta por e-mail e **repõem o cookie `user_id`** para a conta (favoritos do
convidado são preservados/mesclados). `User`/`UserDocument` ganharam `picture`/`googleId`; novo
`findByEmail`. Login ativável via profile `oauth` +
[application-oauth.yaml](src/main/resources/application-oauth.yaml) (`GOOGLE_CLIENT_ID`/`SECRET`).
Sem credenciais o app sobe normal e o uso anônimo segue 100%. *Observação:* o fluxo OIDC ponta-a-ponta
só pode ser exercitado com credenciais Google reais; validados o boot do contexto e o site público.

**FUNC-06** — header ([fragments/layout.html](src/main/resources/templates/fragments/layout.html))
mostra o estado de login via `currentUser`/`oauthEnabled`
([CurrentUserAdvice](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/CurrentUserAdvice.kt)):
avatar + nome + "Sair" quando autenticado, "Entrar com Google" quando habilitado, ou "Convidado"
caso contrário. Estilos do chip/logout em [common.css](src/main/resources/static/css/common.css).

**FUNC-03** — nova rota `/usuario`
([UserController](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/UserController.kt))
com template [usuario.html](src/main/resources/templates/usuario.html): cartão de perfil (conta Google
ou visitante anônimo, com badge de admin) e grade de favoritos via
[FindFavoriteAnimesUseCase](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/FindFavoriteAnimesUseCase.kt).
Acessível a autenticados e convidados.

**FUNC-09** — sinopse com abas **PT/EN** em [anime.html](src/main/resources/templates/anime.html):
os campos `synopsis` (EN) e `synopsisPtBr` (PT) já existiam no domínio; as abas só aparecem quando há
ambos os idiomas. Toggle `switchSynopsis` em [anime.js](src/main/resources/static/js/anime.js),
estilos em [anime.css](src/main/resources/static/css/anime.css).

**FUNC-02** — endpoint `GET /api/v1/user/favorites`
([UserAPIController](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/UserAPIController.kt))
+ página dedicada `/favoritos`
([FavoritesController](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/FavoritesController.kt)
→ [favoritos.html](src/main/resources/templates/favoritos.html)) com link no menu. O botão da página de
detalhe agora **reflete o estado** (✓ Na Lista / + Minha Lista) via `isFavorited` no
[AnimeController](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/AnimeController.kt).
*No caminho, corrigido um bug de render do Thymeleaf 3.1: `th:on*` não aceita expressão string — usado
o inlining `[[${anime.id}]]`.*

**F2** — coleção `site_integrations` no MongoDB
([SiteIntegrationDocument](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/documents/SiteIntegrationDocument.kt)
+ [SiteIntegrationMongoRepository](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/repositories/SiteIntegrationMongoRepository.kt)).
O antigo [SiteIntegrationRepository](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/repositories/SiteIntegrationRepository.kt)
virou fachada: semeia a coleção uma única vez (24 sites migrados), respeita `enabled` nas filas
FAST/MEDIUM/SLOW e **persiste** `lastExecution*` (corrige o bug do update em memória). O conteúdo do
script segue em `resources/scripts/` (documento guarda só o nome do arquivo). Validado: 24 sites
seedados e servidos pela API.

**FUNC-05** — comentários agora têm `status` (`PENDING`/`APPROVED`/`REJECTED`) em
[Anime](src/main/kotlin/br/com/achimid/animesachimidv2/domains/Anime.kt)/`AnimeCommentDocument`
(antigos contam como aprovados). Novos entram como **PENDING** e o detalhe só mostra **aprovados**;
o autor recebe aviso de "aguardando aprovação". Validação de tamanho (3–1000) no
`AnimeAPIController`. Moderação via
[ModerateCommentUseCase](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/ModerateCommentUseCase.kt)
+ [AdminCommentAPIController](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/AdminCommentAPIController.kt)
(`/api/v1/admin/comments/...`). *No caminho, corrigido o mapeamento de `isAdmin` (mapper manual) que
estava sempre `false`.* Validado: comentário fica oculto até aprovação, e aparece após aprovar.

**FUNC-04** — área `/admin`
([AdminController](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/AdminController.kt)
→ [admin.html](src/main/resources/templates/admin.html)) com **dashboard de scraping** (status +
habilitar/desabilitar e trocar a fila dos sites, via F2 — [AdminSiteAPIController](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/AdminSiteAPIController.kt))
e **moderação de comentários** (FUNC-05). Acesso restrito a admins via
[AdminAccessChecker](src/main/kotlin/br/com/achimid/animesachimidv2/configurations/AdminAccessChecker.kt)
(cookie `user_id` → `isAdmin`; não-admin é redirecionado, APIs retornam 403). Link para o painel na
página do usuário. Validado: gating (302/403), toggle persistido no Mongo e moderação ponta-a-ponta.

**FUNC-14** — o seeder de [SiteIntegrationRepository](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/repositories/SiteIntegrationRepository.kt)
virou **idempotente por nome** (insere só os faltantes, propagando novos sites para bancos já
populados sem sobrescrever ajustes do admin). Adicionados 8 candidatos (scripts já existentes: Animes
365, Central de Animes, Animes Games, Animes Flix, Animes BR, Animes Online Red, Go Animes, Bakashi)
como **desabilitados** para triagem/validação pelo painel admin (FUNC-04). Validado: Mongo passou a 32
sites (8 desabilitados); as filas FAST/MEDIUM/SLOW continuam ignorando os desabilitados.

**FUNC-08** — agenda híbrida em
[FindCalendarReleaseUseCase](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/FindCalendarReleaseUseCase.kt):
além do SubsPlease, agora mescla os **animes em exibição já ingeridos** (status `AIRING`) usando o
`broadcast.day`/`time` do Jikan (via `AnimeGateway.findAiringScheduled`), unificados por **id do anime**
(sem duplicar). Bônus: se o SubsPlease falhar, monta uma semana vazia e preenche com os dados locais —
a agenda **deixa de ficar 500** (corrige o `execute()!!`). Validado: com SubsPlease indisponível, os 10+
animes locais em exibição apareceram no dia certo (ex.: Honzuki no Sábado).

**FUNC-10** — recomendação por **conteúdo** em
[FindRecommendationsUseCase](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/FindRecommendationsUseCase.kt):
deixou de ser aleatória. Na home usa o **perfil do usuário** (gêneros dos favoritos); na página de
detalhe, "Animes Parecidos" usa os gêneros do **anime atual** — ranqueando candidatos pela sobreposição
de tags e pelo score (`AnimeGateway.findByTags`), com fallback aleatório para quem não tem sinal.
O `AnimeDocumentMapper` agora persiste **themes + demographics** do Jikan junto dos gêneros em `tags`
(mais sinal e chips mais ricos). Validado: no detalhe de um anime de Mystery/Adventure/Comedy, os 3
recomendados compartilhavam exatamente esses gêneros.

**FUNC-13** — primeira fonte extra de enriquecimento: **AniList (GraphQL)** para o **próximo episódio**.
Novo client Feign
[AniListAPIClient](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/http/anilist/AniListAPIClient.kt)
(`external.anilist.url`) + [AniListAPIGateway](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/http/AniListAPIGateway.kt)
que consulta `Media(idMal:…).nextAiringEpisode` (o `@Id` do anime no Mongo já é o `mal_id`).
[FindNextEpisodeUseCase](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/FindNextEpisodeUseCase.kt)
é `@Cacheable("nextEpisodeCache")` (30 min, em `CacheConfig`) e **tolerante a falha** (retorna `null` se
a AniList cair ou o anime não estiver no ar). O [AnimeController](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/site/AnimeController.kt)
busca o próximo episódio **em paralelo** com as recomendações; o card "⏭️ Próximo Episódio" com countdown
(`NextEpisode.countdown()` → "6d 12h") só aparece quando há dado
([anime.html](src/main/resources/templates/anime.html) + [anime.css](src/main/resources/static/css/anime.css)).
Validado: Honzuki ("estreia em 6d 12h"), Iruma-kun 4ª temporada e Saijo no Osewa renderizaram o countdown;
animes fora do ar não exibem o card. *Base para TMDB/Kitsu e para evoluir a precedência por campo no `merge`.*

**FUNC-07** — **central de notificações in-app** (fase 1 do faseamento sugerido). Nova coleção
`notifications` ([NotificationDocument](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/documents/NotificationDocument.kt)
+ [NotificationRepository](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/repositories/NotificationRepository.kt)
+ [NotificationGateway](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/outputs/mongodb/NotificationGateway.kt)).
Quando um `Release` é criado, o já existente
[AfterCreateReleaseUserCase](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/AfterCreateReleaseUserCase.kt)
(`@Async`) chama o novo [NotifyFavoritesUseCase](src/main/kotlin/br/com/achimid/animesachimidv2/usecases/NotifyFavoritesUseCase.kt),
que busca os usuários que favoritaram o anime (`UserRepository.findByFavoritesContaining`) e cria uma
notificação por usuário — **deduplicando por (usuário, anime, episódio)** para não repetir a cada mirror.
Endpoints `GET /api/v1/user/notifications` e `POST /api/v1/user/notifications/read`
([NotificationAPIController](src/main/kotlin/br/com/achimid/animesachimidv2/gateways/inputs/http/api/NotificationAPIController.kt)),
escopados pelo cookie `user_id` (funciona para autenticados **e** convidados). **Sino com contador** de não
lidas no header ([fragments/layout.html](src/main/resources/templates/fragments/layout.html)), com dropdown,
marcação automática como lido ao abrir e escape de HTML — JS em
[common.js](src/main/resources/static/js/common.js), estilos em
[common.css](src/main/resources/static/css/common.css). Validado ponta-a-ponta: lista ordenada por mais
recente, badge de não lidas e `POST .../read` marcando todas como lidas (updateMulti). *Fases 2/3 (Web Push,
Telegram) e preferências por canal ficam para depois — ver bloco FUNC-07 abaixo.*

## Sumário (pendentes)

| ID | Funcionalidade | Esforço | Prio | Depende |
|----|----------------|:------:|:---:|---------|
| FUNC-12 | Internacionalização (i18n) | 🔴 G | ○ | — |
| FUNC-07 (fase 3) | Telegram: vincular chat + alertas | 🔴 G | ○ | F1 |
| FUNC-13 (TMDB/Kitsu) | Imagens/trailers + sinopse PT | 🟡 M | ◐ | — |

---

## Implementadas na onda de 14/06/2026

### FUNC-07b — Web Push (fase 2 de FUNC-07)

**Implementado.** Ao adicionar um anime aos favoritos pela primeira vez, o site exibe um prompt
solicitando permissão de notificações push. Se o usuário aceitar, o Service Worker registrado
(`/sw.js`) cria uma `PushSubscription` via VAPID e a envia para `POST /api/v1/user/push/subscribe`.
O backend armazena endpoint + chaves (`p256dh`, `auth`) por usuário e, quando um release é criado
via `AfterCreateReleaseUserCase`/`NotifyFavoritesUseCase`, envia o push via a API de Web Push.

**Arquivos-chave:**
- `NotifyFavoritesUseCase` — filtra usuários e despacha push
- `UserAPIController` — endpoint `/push/subscribe`
- `sw.js` — service worker (cache de assets + handler de push)
- `anime.js` — prompt inline ao favoritar

---

### FUNC-15 — Preferência de site de notificação por anime

**Implementado.** Na página `/favoritos`, cada card exibe um botão 🔔 que abre um painel flutuante
com checkboxes — um por site de integração habilitado. O usuário pode escolher um ou mais sites;
somente releases vindos desses sites geram notificação para aquele anime. Se nenhum site for
selecionado, o comportamento é "qualquer site" (padrão anterior).

**Modelo de dados:** `User.notificationSitePreferences: Map<String, Set<String>>` — chave=animeId,
valor=conjunto de nomes de sites.

**Filtragem em `NotifyFavoritesUseCase`:** `if (!userPrefs.isNullOrEmpty() && fromSite !in userPrefs) skip`.

**Arquivos-chave:**
- `User.kt` / `UserDocument.kt` — novo campo `notificationSitePreferences`
- `UserGateway.kt` — método `updateNotificationSitePreference`
- `UpdateNotificationSitePreferenceUseCase.kt` — caso de uso
- `UserAPIController` — `PUT/DELETE /api/v1/user/notification-preference/{animeId}`
- `NotifyFavoritesUseCase` — filtro por site
- `AfterCreateReleaseUserCase` / `CreateReleaseUserCase` — repasse do `fromSite`
- `FavoritesController` — injeta `siteIntegrations` e `notifPrefs` no model
- `favoritos.html` + `favoritos-notify.js` + `favoritos.css`

---

### FUNC-16 — Transferência de identidade anônima entre dispositivos

**Implementado.** Na aba "Configurações" de `/usuario`, o usuário pode ver seu `user_id` atual,
copiá-lo para a área de transferência e importar um ID de outro dispositivo. O endpoint
`POST /api/v1/user/transfer` valida o UUID, verifica a existência do usuário e atualiza o cookie
`user_id` com expiração de 1 ano — permitindo continuar a mesma sessão (favoritos, preferências) em
outro navegador ou máquina sem login com Google.

**Arquivos-chave:**
- `UserAPIController` — endpoint `POST /api/v1/user/transfer` + DTO `TransferUserRequest`
- `FindUserUseCase` — verificação de existência antes de emitir o cookie
- `usuario.html` — seção "Identificação" com campo readonly + botão copiar + input importar
- `user.js` — handlers de cópia e importação via `document.cookie`

---

### FUNC-17 — Gerenciamento centralizado de favoritos

**Implementado.** Toda a gestão de favoritos foi centralizada em `/favoritos`: remoção de animes,
configuração de site de notificação e navegação para o detalhe. A página `/usuario` passou a exibir
apenas um resumo com link para `/favoritos`, evitando duplicidade de controles.

---

## Seção 1 — Fundações transversais

Três bases que, sozinhas, não são "features visíveis", mas destravam metade da lista. Vale tratá-las
como épicos próprios.

## Seção 2 — Funcionalidades

### FUNC-07 — Notificações de favoritos lançados · 🔴 G · ◐ · (F1)

**Objetivo.** Avisar a pessoa quando um anime que ela favoritou ganha episódio novo. Esse é o
"gancho de retorno" do produto.

**Ponto de integração.** Quando `CreateReleaseUserCase` cria um `Release`, já existe o
`AfterCreateReleaseUserCase` (`@Async`) que dispara a tradução — é o lugar natural para também
**buscar usuários cujo `favorites` contém o anime** e enfileirar notificações.

**Canais (faseamento recomendado):**
1. **Central de notificações in-app (base — começar por aqui).** Coleção `notifications`
   (`userId`, `animeId`, `episode`, `read`, `createdAt`); sino no header com contador de não lidas;
   endpoints `GET /api/v1/user/notifications` e `POST .../read`. Não depende de permissão do navegador
   nem de serviço externo — é o alicerce dos outros canais.
2. **Web Push (navegador).** VAPID + Service Worker (liga com a intenção de **PWA** no `MELHORIAS.md`):
   o usuário opta-in, guardamos a `PushSubscription`, e o backend envia push mesmo com o site fechado.
   Requer HTTPS (já há) e consentimento (liga com FUNC-11).
3. **App de mensagem (Telegram).** O projeto **já fala com o Telegram** em
   [common.js](src/main/resources/static/js/common.js) (envio de sugestões) — porém com **token
   hardcoded no front** (mover para env var + endpoint no backend; ver SEC no `MELHORIAS.md`).
   Reaproveitar essa integração para, opcionalmente, o usuário vincular um chat e receber avisos lá.

**Preferências.** Cada usuário escolhe canais ligados/desligados (na página de usuário, FUNC-03).
**Cuidado com volume:** dedupe por (usuário, anime, episódio) e _digest_ para quem tem muitos favoritos.

**Arquivos.** `AfterCreateReleaseUserCase`, novo `NotificationGateway`/documento/use cases, endpoints,
sino no `fragments/layout.html`, Service Worker em [static/](src/main/resources/static/), preferências (FUNC-03).

---

### FUNC-12 — Internacionalização (i18n) · 🔴 G · ○

**Problema.** Não há i18n: zero `messages.properties`, zero `#{...}`, tudo hardcoded em PT-BR.

**Como implementar.**
1. `MessageSource` + arquivos `messages_pt_BR.properties` e `messages_en.properties` em `resources`.
2. `LocaleResolver` (cookie/`Accept-Language`) + interceptor `?lang=` para troca manual; seletor de
   idioma no header.
3. **Externalizar strings** dos templates para `#{chave}` — trabalho grande porque toca **todos** os
   templates; fazer **faseado por página** (home → detalhe → listagem → calendário → footer).
4. Distinguir **UI** (i18n estática) de **conteúdo** (sinopse já traduzida via LibreTranslate — FUNC-09).

**Arquivos.** `configurations/` (config de locale), `resources/messages_*.properties`, todos os
templates. Esforço alto; priorizar depois das fundações.

---

### FUNC-13 — Mais APIs de enriquecimento · 🟡 M · ◐

**Problema.** O enriquecimento vem só do **Jikan** (MyAnimeList). Lacunas: gêneros/temas mais ricos,
relações entre temporadas, trailers, e sinopses já em PT.

**Candidatas.**
- **AniList (GraphQL):** gêneros/tags ponderadas, relações (sequência/prequela), staff, próximo episódio
  (`airingSchedule`) — ótimo também para FUNC-08 e FUNC-10.
- **TMDB:** imagens de alta qualidade, backdrops e trailers (YouTube).
- **Kitsu:** por vezes traz sinopse em PT, reduzindo dependência do LibreTranslate.

**Como implementar.** Seguir o padrão atual: novo `@FeignClient` em
`gateways/outputs/http/{servico}/` + `merge(...)` no `AnimeDocumentMapper` (como o `merge(anime, jikan)`
existente), com `@Cacheable` e tratamento de rate limit (liga com PERF do Jikan no `MELHORIAS.md`).
Definir **precedência** de fontes por campo (ex.: imagem TMDB > Jikan; gêneros = união AniList ∪ Jikan).

**Arquivos.** Novos clients/gateways Feign, `AnimeDocumentMapper`, `application.yaml` (`external.*.url`),
`CacheConfig` (novos caches).

---

## Seção 3 — Melhorias de design (polir identidade)

A direção aqui é **evolução, não ruptura**: manter a identidade atual — fundo dark
(`--bg-dark: #050505`), cards `#121212`, **verde neon** `#2ef861` e **laranja** `#ff9900`, com
Bungee no logo e Inter/Righteous nos títulos. Tudo abaixo refina essa base. Todas as novas páginas
(FUNC-03, FUNC-04, FUNC-11…) devem seguir `fragments/layout.html` e o sistema de tokens (F3).

### D1 — Tema claro opcional · 🟡 M · ◐ · (F3)

Sobre os tokens semânticos de **F3**, criar um tema claro com `[data-theme="light"]` redefinindo os
tokens (`--color-bg`, `--color-surface`, `--color-text`…), mantendo verde/laranja como acentos.
Alternância no header (☀️/🌙), preferência salva em `localStorage`, respeitando `prefers-color-scheme`
no primeiro acesso. O verde neon precisa de um tom levemente dessaturado no claro para contraste/AA.

### D2 — Refino do dark atual · 🟢 P · ◐

- **Escala de superfícies:** hoje praticamente só `#050505`/`#121212`. Introduzir 3–4 níveis
  (`surface`, `surface-2`, `surface-3`) para hierarquia (header, card, card-hover, modal).
- **Acessibilidade:** `--text-gray: #a0a0a0` sobre `#050505` fica no limite do AA; aumentar contraste
  do texto secundário; **foco visível** consistente (`:focus-visible`) em links/botões/inputs.
- **Estados:** padronizar hover/active/disabled (hoje variam entre os 5 CSS).
- Reduzir uso do verde neon em **áreas grandes** (cansa a vista); mantê-lo como **acento** pontual.

### D3 — Sistema de componentes · 🟡 M · ◐ · (F3)

Unificar, sobre os tokens, os componentes que hoje repetem com pequenas diferenças entre
`common`/`home`/`anime`/`animes`/`calendar`:
- **Cards de anime** (grid da home, listagem, recomendados, favoritos) → um componente só.
- **Badges** (`ep-badge`, DUB/LEG, qualidade, status airing/completo).
- **Botões** (`btn`, `btn-secondary`, `btn-download`, `btn-server`) → variantes coesas.
- **Chips do calendário** e itens de lista.

Isso reduz CSS duplicado e dá consistência a toda página nova.

### D4 — Micro-interações, loading e estados vazios · 🟡 M · ◐

- **Skeletons** no lugar de telas em branco enquanto a home/listagem carrega (liga com UX no
  `MELHORIAS.md`).
- **Estados vazios/erro** explícitos: favoritos vazios (FUNC-02), "fim da lista" no exibir-mais
  (FUNC-01), falha de busca, agenda sem itens no dia.
- Transições suaves (hover de card, abertura de accordion de episódios, abas de sinopse de FUNC-09).
- **Lazy-load + dimensões nas imagens** (liga com Core Web Vitals no `MELHORIAS.md`).

### D5 — Tipografia e ritmo · 🟢 P · ○ · (F3)

Escala tipográfica e de espaçamento via tokens `--space-*`/`--font-size-*`; uso consistente de
Bungee (logo) / Righteous (títulos) / Inter (corpo) — hoje há mistura com "Segoe UI" como `--font-main`.

### D6 — Inspiração de outros sites de anime

Padrões observados em referências populares, sugeridos **dentro da identidade atual** (não copiar visual):
- **Hover card com sinopse rápida** (AniList/MAL): ao passar o mouse num card, prévia com sinopse curta,
  gênero e nota — o projeto já tem tooltip em [common.js](src/main/resources/static/js/common.js), dá
  para evoluir.
- **Trilha "Continuar acompanhando"** (Crunchyroll): faixa horizontal dos favoritos/últimos vistos na
  home (encaixa com FUNC-02/FUNC-03).
- **Filtros por gênero/temporada/ano** (AniList): a página `animes.html` já tem seletores de ordenação
  e gênero **não implementados** — completá-los (sinergia com FUNC-10/FUNC-13).
- **Countdown para o próximo episódio** na agenda (LiveChart/AnimeSchedule): "faltam 2h 13min" por item
  (encaixa com FUNC-08 + AniList `airingSchedule` de FUNC-13).
- **Badges DUB/LEG e qualidade** bem visíveis nos cards (dados já existem em parte dos `Release`).

---

## Seção 4 — Roadmap sugerido

Sequência em **ondas**, respeitando dependências. Cada onda entrega valor sozinha.

### Onda 1 — Quick wins (sem novas fundações)
Coisas de alto retorno que não dependem de auth:
- ✅ **FUNC-01** — corrigir o "Exibir mais" (bug visível). *(feito)*
- ✅ **FUNC-09** — detalhe bilíngue (dados já existem). *(feito)*
- ✅ **FUNC-11** — páginas legais + consentimento de cookies (corrige 404 do footer; conformidade). *(feito)*
- ✅ **F3** — tokens semânticos *(feito)*; D2/D5 (refino do dark) pendentes.

### Onda 2 — Fundação de conta
- ✅ **F1** — auth Google + anônimo com merge de dados *(feito)* → habilitou
  ✅ **FUNC-06** (UX de login), ✅ **FUNC-03** (página do usuário) e ✅ **FUNC-02** (favoritos) *(feitos)*.

### Onda 3 — Admin & qualidade de conteúdo
- ✅ **F2** (sites no MongoDB), ✅ **FUNC-04** (área admin), ✅ **FUNC-05** (moderação) e
  ✅ **FUNC-14** (triagem de mais sites) — *todos feitos*.

### Onda 4 — Engajamento & relevância
- ✅ **FUNC-08** — agenda híbrida (cobre os ingeridos que faltavam) *(feito)*.
- ✅ **FUNC-10** — recomendações inteligentes (themes persistidos, perfil do usuário) *(feito)*.
- ✅ **FUNC-07** — notificações de favoritos: **central in-app** (fase 1) *(feito)*.
- ✅ **FUNC-07b** — **Web Push** (fase 2): permissão ao favoritar, VAPID, service worker *(feito)*.
- ✅ **FUNC-15** — preferência de site de notificação por anime (multi-site) *(feito)*.
- ✅ **FUNC-16** — transferência de identidade anônima entre dispositivos *(feito)*.
- ✅ **FUNC-17** — gerenciamento centralizado de favoritos em `/favoritos` *(feito)*.

### Onda 5 — Alcance & polimento
- ✅ **FUNC-13** — mais APIs: **AniList** (próximo episódio/countdown) *(feito)*. TMDB/Kitsu
  (imagens/trailers/sinopse PT) e precedência por campo no `merge` seguem pendentes.
- **FUNC-12** — i18n (faseado por página).
- **FUNC-07 fase 3** — Telegram: vincular chat e receber alertas.
- **D1/D3/D4/D6** — tema claro, sistema de componentes, micro-interações e inspirações de UX.

---

## Pré-requisitos técnicos que aparecem com frequência

Estes itens do [MELHORIAS.md](MELHORIAS.md) são **bloqueadores ou facilitadores** recorrentes — vale
encará-los junto das ondas acima:
- **SEC** do callback público e do cookie `user_id` sem assinatura → base de F1/FUNC-07.
- **SEC** do XSS via `th:utext` → necessário para FUNC-05/FUNC-09 (render seguro de texto).
- **SEC** do `POST .../extraction/all/run` público → fechar dentro de F1/FUNC-04.
- **BUG** do `findRandom` com página fixa e do `updateByName` sem persistir → tocam FUNC-10 e F2.
- **Token Telegram hardcoded** no front → mover para backend/env var antes de FUNC-07.
- **Cobertura de testes ~zero** → ao mexer em auth, moderação e recomendações, adicionar testes
  (são justamente as áreas sensíveis).

## Observações finais

- Este documento é **roadmap**, não implementação. A sugestão é começar por **FUNC-01** (correção
  visível e isolada) e, em paralelo, **F3** (tokens) — ambos sem dependências.
- Ao implementar cada item, seguir as convenções do [CLAUDE.md](CLAUDE.md) (nomenclatura de use
  cases/gateways, cache declarado em `CacheConfig`, strings em PT-BR, etc.).
- Manter este arquivo vivo: marcar itens concluídos e mover dívidas técnicas descobertas para o
  [MELHORIAS.md](MELHORIAS.md).
