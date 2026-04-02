# [Animes Achimid](https://animes.achimid.com.br/)

[![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![Spring Boot](https://img.shields.io/badge/spring--boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Google Gemini](https://img.shields.io/badge/google%20gemini-8E75B2?style=for-the-badge&logo=google%20gemini&logoColor=white)](https://ai.google.dev/)
[![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)](https://developer.mozilla.org/en-US/docs/Web/JavaScript)
[![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/HTML)
[![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/CSS)
[![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)](https://github.com/features/actions)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Cloudflare](https://img.shields.io/badge/Cloudflare-F38020?style=for-the-badge&logo=Cloudflare&logoColor=white)](https://www.cloudflare.com/)

> Feito por um amante de animes. De fã para fã.

**Animes Achimid** é um agregador de lançamentos de animes da temporada, coletando dados de diversos fansubs, sites de animes e fontes de cultura japonesa. Utiliza tecnologias modernas para scraping, armazenamento e apresentação de informações atualizadas.

## 🌐 Links Úteis
- [Site Principal](https://animes.achimid.com.br)
- [AnimeX](https://animex.achimid.com.br)

## 📊 Sites Monitorados

### Ativos
- **Erai-raws (Nyaa)**: [https://nyaa.si/](https://nyaa.si/) (Fonte de torrents para releases em alta qualidade)

### Inativos (já monitorados anteriormente)
Estes sites foram monitorados no passado mas estão desabilitados atualmente:
- Animes 365: [https://animes365.net/](https://animes365.net/)
- Crunchyroll: [https://www.crunchyroll.com/pt-br/simulcastcalendar?filter=premium](https://www.crunchyroll.com/pt-br/simulcastcalendar?filter=premium)
- Anitube VIP: [https://www.anitube.vip/](https://www.anitube.vip/)
- AnimesRoll: [https://anroll.tv/](https://anroll.tv/)
- Animes HD: [https://animeshd.to/](https://animeshd.to/)
- Animes Online CC: [https://animesonlinecc.to/episodio/](https://animesonlinecc.to/episodio/)
- Goyabu: [https://goyabu.to/lancamentos](https://goyabu.to/lancamentos)
- Animes Online FHD: [https://animesonlinefhd.vip/](https://animesonlinefhd.vip/)
- Hinata Soul: [https://www.hinatasoul.com/](https://www.hinatasoul.com/)
- Anime Q: [https://animeq.blog/](https://animeq.blog/)
- Animes Online Cloud: [https://animesonline.cloud/](https://animesonline.cloud/)
- Animes Drive: [https://animesdrive.online/episodio](https://animesdrive.online/episodio)
- Animes Up: [https://www.animesup.info/](https://www.animesup.info/)
- Subs Please (ENG): [https://subsplease.org/](https://subsplease.org/)
- Dark Animes: [https://darkmahou.io](https://darkmahou.io)
- Anime Fire: [https://animefire.plus/](https://animefire.plus/)
- Top Animes: [https://topanimes.net/](https://topanimes.net/)
- World Fansub: [https://worldfansub.xyz/#lancamentos](https://worldfansub.xyz/#lancamentos)
- AnimeFlix (ENG): [https://animeflix.team/](https://animeflix.team/)
- Anime NSK: [https://packs.ansktracker.com/](https://packs.ansktracker.com/)
- Animes Digital: [https://animesdigital.org/lancamentos](https://animesdigital.org/lancamentos)
- Central de Animes: [https://centraldeanimes.xyz/](https://centraldeanimes.xyz/)
- Animes Games: [https://animesgames.cc/lancamentos](https://animesgames.cc/lancamentos)
- Animes Flix: [https://animesflix.net/](https://animesflix.net/)
- Animes BR: [https://animesbr.tv/episodios/](https://animesbr.tv/episodios/)
- Animes Online Red: [https://animesonline.red/](https://animesonline.red/)
- Go Animes: [https://www.goanimes.vip/](https://www.goanimes.vip/)
- Bakashi: [https://topanimes.net/](https://topanimes.net/)

## 🏗️ Arquitetura

O projeto segue a **arquitetura limpa (Clean Architecture)** em Kotlin com Spring Boot:

- **Domínios**: Entidades como `Anime`, `Release`, `SiteIntegration`.
- **Casos de Uso**: Lógica de negócio (ex.: `FindAnimesUseCase`).
- **Gateways**: Interfaces externas, incluindo controladores REST e clientes Feign.
- **Cron**: Tarefas agendadas para scraping e sincronização.
- **Infraestrutura**: MongoDB para persistência, Thymeleaf para templates web.

Fluxo de dados: Jobs cron triggam casos de uso para buscar de APIs externas ou executar scripts JS via Puppeteer Executor, processam callbacks e salvam no banco.

## 🔗 APIs e Serviços

Integrações externas:
- **Jikan API**: Dados do MyAnimeList.
- **Subsplease API**: Informações de releases.
- **Google Gemini**: Potencial para tradução automática.

## 🤝 Contribuição

Contribuições são bem-vindas! Siga estes passos:

1. Fork o projeto.
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`).
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`).
4. Push para a branch (`git push origin feature/nova-feature`).
5. Abra um Pull Request.

### Diretrizes
- Siga as convenções do projeto (veja `AGENTS.md`).
- Adicione testes para novas funcionalidades.
- Atualize a documentação conforme necessário.

## 💡 Sugestões de Melhorias

- [ ] Deixe seu feedback aqui
- [ ] Corrigir descrições para português ou versões alternativas
- [ ] Implementar tradução automática das descrições (usando Google Gemini)
- [ ] Adicionar mais informações nos scripts (linguagens, temporada, tipo dublado/legendado)
- [ ] Adicionar novos sites para monitorar:
  - https://fenixfansub.net/
  - https://www.animesonline22.com/
  - https://www7.gogoanime.me/
  - https://xpanimes.com/
  - https://meusanimes.biz/
  - https://animesonline.fan/
  - https://www.animesking.com/
  - https://animesonehd.co/anime/
  - https://anitaku.to/home.html
  - https://animesvicio.biz/
  - https://otakuanimess.cc/
  - https://animesonlineclub.com/
  - https://ww33.animesonline.online/
  - https://aniwave.to/home
  - https://www.animegg.org/
  - https://www1.kickassanime.mx/
  - https://everythingmoe.com/
  - https://animetake.tv/
  - https://www.animeschyroll.online/
  - https://animetvonline.blog/

Para sugestões, abra uma [issue](https://github.com/seu-usuario/animes-achimid-v2/issues) no GitHub.

## 📄 Licença

Este projeto é distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---

⭐ Se gostou do projeto, dê uma estrela no GitHub!
