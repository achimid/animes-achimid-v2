# Melhorias da Página Home — Animes Achimid

> Análise gerada em **2026-06-14** com base na leitura do código atual da home (`home.html`, `home.css`, `home.js`).
> Cada sugestão descreve o problema atual, a solução proposta, os arquivos envolvidos e o esforço estimado.

---

## Índice rápido

| # | Melhoria | Área | Impacto | Esforço |
|---|---|---|---|---|
| H-01 | [Skeleton Loading nos cards](#h-01-skeleton-loading-nos-cards) | UX / Visual | Alto | Pequeno |
| H-02 | [Hover tooltip com preview do anime](#h-02-hover-tooltip-com-preview-do-anime) | UX / Visual | Alto | Médio |
| H-03 | [Chips de filtro rápido nos lançamentos](#h-03-chips-de-filtro-rápido-nos-lançamentos) | UX / Funcional | Médio | Pequeno |
| H-04 | [Toast de novo episódio disponível](#h-04-toast-de-novo-episódio-disponível) | UX / Funcional | Médio | Pequeno |
| H-05 | [Scroll infinito](#h-05-scroll-infinito) | UX / Funcional | Médio | Pequeno |
| H-06 | [Widget de estatísticas expandido](#h-06-widget-de-estatísticas-expandido) | Visual / Sidebar | Baixo | Pequeno |
| H-07 | [Seção "Em destaque hoje" dinâmica](#h-07-seção-em-destaque-hoje-dinâmica) | Layout / Funcional | Alto | Grande |

---

## H-01 Skeleton Loading nos cards

### Problema atual
Quando o usuário clica em "Exibir mais" ou quando o auto-reload dispara a cada 5 minutos, a grid de lançamentos fica momentaneamente vazia enquanto a requisição ao backend é processada. Isso cria um "flash" de tela vazia que dá a impressão de que o site travou ou o conteúdo desapareceu — especialmente perceptível em conexões mais lentas.

### Solução proposta
Antes de iniciar cada requisição, preencher a grid com cards placeholder que imitam o tamanho e formato dos cards reais, mas exibem um fundo cinza-escuro com animação de "shimmer" (gradiente deslizante da esquerda para a direita). Quando a requisição terminar, substituir os placeholders pelos cards reais. Essa técnica é a mesma usada por Netflix, YouTube e Crunchyroll.

### Como ficaria
```
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│▓▓▓▓▓▓▓▓▓▓│ │▓▓▓▓▓▓▓▓▓▓│ │▓▓▓▓▓▓▓▓▓▓│ │▓▓▓▓▓▓▓▓▓▓│ │▓▓▓▓▓▓▓▓▓▓│
│  shimmer │ │  shimmer │ │  shimmer │ │  shimmer │ │  shimmer │
│▓▓▓▓▓▓▓▓▓▓│ │▓▓▓▓▓▓▓▓▓▓│ │▓▓▓▓▓▓▓▓▓▓│ │▓▓▓▓▓▓▓▓▓▓│ │▓▓▓▓▓▓▓▓▓▓│
└──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘
```

### Arquivos envolvidos
- `static/css/home.css` — adicionar `.skeleton-card` e `@keyframes shimmer`
- `static/js/home.js` — chamar `showSkeletons()` antes do `fetch` e `removeSkeletons()` no `.then()`

### Esforço estimado
**Pequeno** — apenas CSS e poucas linhas de JS. Não requer alteração no backend.

---

## H-02 Hover tooltip com preview do anime

### Problema atual
Os cards de anime na grid exibem apenas a capa, o título e o número do episódio. Para saber se vale clicar em determinado anime, o usuário precisa entrar na página completa, o que interrompe o fluxo de navegação e aumenta o número de cliques desnecessários. Isso é especialmente frustrante quando o usuário está procurando algo novo para assistir e quer comparar vários títulos rapidamente.

### Solução proposta
Ao passar o mouse sobre um card (hover com delay de ~300ms para evitar acionamentos acidentais), exibir um painel lateral flutuante — preferencialmente à direita ou à esquerda dependendo da posição do card na tela — contendo:
- Capa em resolução maior
- Título completo e título alternativo (japonês/inglês)
- Score do MyAnimeList com estrela dourada
- Tags de gênero (ex: `Ação`, `Fantasia`, `Isekai`)
- Primeiras 2-3 linhas da sinopse em português
- Status e número de episódios

Os dados já estão disponíveis via `/api/v1/anime/{slug}` ou podem ser embutidos como `data-*` attributes no card gerado pelo template Thymeleaf, evitando uma requisição extra.

### Como ficaria
```
┌──────────┐
│          │   ┌─────────────────────────────────────┐
│  CAPA    │◄──│ 🎌 Nome do Anime                    │
│  CARD    │   │ ★ 8.34  •  Ação, Isekai, Fantasia   │
│          │   │─────────────────────────────────────│
└──────────┘   │ Sinopse resumida em até 3 linhas... │
               │                                     │
               │ 📺 Em exibição  •  24 episódios     │
               └─────────────────────────────────────┘
```

### Arquivos envolvidos
- `templates/home.html` — adicionar `data-score`, `data-synopsis`, `data-tags` etc. nos cards gerados pelo Thymeleaf
- `static/css/home.css` — estilos do `.anime-tooltip-popup` com `position: fixed` e animação de entrada
- `static/js/home.js` — lógica de `mouseenter`/`mouseleave` com delay e posicionamento inteligente

### Esforço estimado
**Médio** — requer atenção no posicionamento do tooltip (cards nas bordas da tela precisam inverter a direção) e cuidado com mobile (no touch o tooltip não faz sentido).

---

## H-03 Chips de filtro rápido nos lançamentos

### Problema atual
A seção "Últimos Lançamentos" exibe todos os episódios sem distinção — dublados, legendados, torrents e filmes aparecem misturados. Um usuário que só assiste dublado precisa percorrer toda a lista para encontrar o que procura. Não existe nenhum mecanismo de filtragem na grid principal da home.

### Solução proposta
Adicionar uma fila de chips logo abaixo do título da seção, antes da grid:

```
[ Todos ] [ Dublado ] [ Legendado ] [ Torrent ] [ Filme ]
```

Ao clicar em um chip, filtrar os cards visíveis por tipo via JS, sem requisição ao backend — a informação do tipo (`animeType`) já está presente em cada card. O chip ativo fica destacado em verde. Dois cliques no mesmo chip volta para "Todos".

### Observação técnica
O campo `animeType` retornado pela API (`/api/v1/release`) já classifica cada release. Os cards gerados dinamicamente pelo JS (`showMoreButton`) e os estáticos do Thymeleaf precisariam receber um `data-type="${anime.animeType}"` para que o filtro possa inspecioná-los.

### Arquivos envolvidos
- `templates/home.html` — adicionar `data-type` nos cards estáticos iniciais e os chips HTML
- `static/css/home.css` — estilo dos chips (similar aos chips da página de calendário)
- `static/js/home.js` — lógica de filtragem por `data-type` e estado do chip ativo

### Esforço estimado
**Pequeno** — sem backend. Atenção para sincronizar o filtro com o "Exibir mais" (carregar só do tipo filtrado ou filtrar no cliente).

---

## H-04 Toast de novo episódio disponível

### Problema atual
O site já possui um auto-reload silencioso a cada 5 minutos (`setInterval(autoReload, 1000 * 60 * 5)`) que atualiza a grid de lançamentos sem avisar o usuário. O resultado é que cards novos aparecem do nada no topo da grid sem nenhum feedback visual — o usuário pode nem perceber que algo mudou.

### Solução proposta
Antes de substituir o conteúdo da grid no auto-reload, comparar os slugs/IDs dos cards atuais com os cards que serão inseridos. Se houver novidades, exibir um toast ("notificação flutuante") discreto no canto inferior da tela:

> **✨ 3 novos episódios disponíveis** — clique para ver

O toast some sozinho após 8 segundos ou ao ser clicado, momento em que a grid é atualizada e o scroll vai suavemente para o topo. Isso mantém o usuário informado sem interromper o que está fazendo.

### Comportamento detalhado
- **Sem novidades** → auto-reload acontece silenciosamente como hoje
- **Com novidades** → toast aparece com contagem; grid só atualiza ao clicar no toast ou após timeout
- **Múltiplos toasts** → empilhar ou substituir o anterior

### Arquivos envolvidos
- `static/css/home.css` — estilos do `.toast-notification` com animação de entrada/saída
- `static/js/home.js` — comparar estado antes/depois do fetch e chamar `showToast(count)`

### Esforço estimado
**Pequeno** — toda a lógica é client-side. O auto-reload já existe, basta instrumentalizá-lo.

---

## H-05 Scroll infinito

### Problema atual
O carregamento de mais lançamentos depende de o usuário encontrar e clicar no botão "Exibir mais" no final da página. Além de exigir uma ação explícita, o botão fica abaixo do fold — especialmente em mobile — e muitos usuários não percebem que existe mais conteúdo. O padrão moderno em feeds de conteúdo é carregar automaticamente ao aproximar do fim da página.

### Solução proposta
Substituir (ou complementar) o botão "Exibir mais" por um `IntersectionObserver` apontado para um elemento sentinela invisível posicionado alguns cards antes do fim da lista. Quando o sentinela entrar na viewport, a próxima página é carregada automaticamente. Um spinner discreto substitui o botão durante o carregamento.

Manter o botão apenas no estado final ("Você chegou ao fim 🎉") para sinalizar que não há mais conteúdo.

### Vantagem sobre o botão
- Funciona melhor no mobile (onde rolar é natural)
- Elimina uma ação desnecessária do usuário
- Aumenta o tempo de sessão na página

### Arquivos envolvidos
- `static/js/home.js` — substituir o listener do `btnLoad` por um `IntersectionObserver` no elemento sentinela
- `templates/home.html` — adicionar `<div id="scroll-sentinel"></div>` após a grid
- `static/css/home.css` — estilo do spinner de carregamento

### Esforço estimado
**Pequeno** — a lógica de paginação já existe. É essencialmente trocar o gatilho de "clique no botão" para "elemento entra na viewport".

---

## H-06 Widget de estatísticas expandido

### Problema atual
A sidebar possui uma caixa de "Estatísticas" que exibe um único número: o total de acessos ao site. É uma informação interessante, mas isolada e pouco contextual. O usuário não consegue perceber, por exemplo, quantos animes estão no catálogo, quantos episódios saíram hoje ou quantos sites o Animes Achimid monitora — dados que demonstram o tamanho e a atividade da plataforma.

### Solução proposta
Expandir o widget para um grid 2×2 com quatro métricas, cada uma com ícone, valor destacado e rótulo:

```
┌────────────────────────────────────┐
│ 📊 Estatísticas                    │
├──────────────┬─────────────────────┤
│  👁 1.234.567 │  🎌 1.053          │
│  Acessos     │  Animes             │
├──────────────┼─────────────────────┤
│  📺 47       │  🌐 28              │
│  Episódios   │  Sites              │
│  hoje        │  monitorados        │
└──────────────┴─────────────────────┘
```

Os valores de "Animes" e "Sites monitorados" já existem no backend. "Episódios hoje" precisaria de um endpoint simples ou ser calculado no `HomeController` ao montar o modelo.

### Arquivos envolvidos
- `gateways/inputs/http/site/HomeController.kt` — incluir contagens no modelo
- `templates/home.html` — substituir `.stats-box-single` pelo grid expandido
- `static/css/home.css` — estilos do novo grid de métricas

### Esforço estimado
**Pequeno** — pequeno ajuste no controller para buscar as contagens; CSS e HTML são simples.

---

## H-07 Seção "Em destaque hoje" dinâmica

### Problema atual
A home possui uma seção de "welcome banner" com uma imagem estática (`banner-small.png`) e um overlay com o texto "Sua Central de Animes / De fã para fã". Apesar de visualmente agradável, essa seção não traz informação nova ao usuário — é decorativa e ocupa um espaço valioso da página que poderia destacar conteúdo real. A imagem é a mesma para todos os visitantes, todos os dias.

### Solução proposta
Substituir o banner estático por uma seção hero dinâmica que destaque automaticamente o anime com mais lançamentos ou mais acessos no dia atual. A seção exibiria:

- **Capa em destaque** — imagem do anime com overlay gradiente
- **Badge "Em destaque hoje"** ou "Mais lançamentos"
- **Título** e título alternativo
- **Score** com estrela dourada
- **Gêneros** em chips
- **Sinopse** em até 3 linhas
- **Botão CTA** → ir para a página do anime

Ao usuário retornar amanhã, o destaque muda automaticamente.

### Como ficaria
```
┌────────────────────────────────────────────────────────────────┐
│▓▓▓▓▓▓▓▓▓▓▓   🔥 EM DESTAQUE HOJE                            ▓▓│
│▓▓CAPA▓▓▓▓▓                                                   ▓▓│
│▓▓▓▓▓▓▓▓▓▓▓   Nome do Anime                                   ▓▓│
│▓▓▓▓▓▓▓▓▓▓▓   ★ 8.7  •  Ação  •  Fantasia  •  Isekai         ▓▓│
│▓▓▓▓▓▓▓▓▓▓▓   Sinopse em até 3 linhas...                      ▓▓│
│▓▓▓▓▓▓▓▓▓▓▓                           [ Ver anime → ]         ▓▓│
└────────────────────────────────────────────────────────────────┘
```

### Alternativa mais simples
Se não houver lógica de "mais acessado do dia" no backend, uma alternativa é destacar um **anime aleatório entre os em exibição** (`status = AIRING`) com score acima de 7.5. Isso ainda é infinitamente mais útil que a imagem estática.

### Arquivos envolvidos
- `usecases/` — novo `FindFeaturedAnimeUseCase` (mais acessado ou aleatório entre airing)
- `gateways/inputs/http/site/HomeController.kt` — adicionar `featuredAnime` ao modelo
- `templates/home.html` — substituir `.welcome-banner` pela nova seção
- `static/css/home.css` — estilos da hero section dinâmica

### Esforço estimado
**Grande** — requer novo use case, possivelmente lógica de ranking diário, e um layout hero significativamente diferente do atual. A alternativa "aleatório entre airing" reduz para esforço **médio**.

---

## Resumo de priorização

Para maximizar impacto com menor esforço, a ordem de implementação recomendada é:

1. **H-01 Skeleton** — melhora percepção de performance imediatamente, qualquer usuário nota
2. **H-04 Toast** — aproveita o auto-reload já existente, pouquíssimo código novo
3. **H-03 Chips de filtro** — muito pedido em sites de anime, sem backend necessário
4. **H-05 Scroll infinito** — fluidez no mobile é muito importante, troca simples de gatilho
5. **H-02 Hover tooltip** — impressionante visualmente, médio esforço
6. **H-06 Estatísticas** — sidebar mais rica, pequeno esforço
7. **H-07 Hero dinâmica** — o maior impacto de todos, mas também o maior esforço
