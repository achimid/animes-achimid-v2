const allRecentData = [
    { name: "29-sai Dokushin Chuuken Boukensha no Nichijou", url: "https://cdn.myanimelist.net/images/anime/1406/119540.webp" },
    { name: "Okiraku Ryoushu no Tanoshii Ryouchi Bouei", url: "https://cdn.myanimelist.net/images/anime/1988/153962.webp" },
    { name: "Odayaka Kizoku no Kyuuka no Susume.", url: "https://cdn.myanimelist.net/images/anime/1151/152479.webp" },
    { name: "Oshi no Ko S3", url: "https://cdn.myanimelist.net/images/anime/1559/154695.webp" },
    { name: "Yuusha Party wo Oidasareta Kiyoubinbou", url: "https://cdn.myanimelist.net/images/anime/1015/138006.webp" },
    { name: "Shibou Yuugi de Meshi wo Kuu.", url: "https://cdn.myanimelist.net/images/anime/4/88698.webp" },
    { name: "Tamon-kun Ima Docchi", url: "https://cdn.myanimelist.net/images/anime/1127/154488.webp" },
    { name: "Dragon Raja S2 (JP)", url: "https://cdn.myanimelist.net/images/anime/1986/120280.webp" },
    { name: "Nier: Automata Ver1.1a Part 2", url: "https://cdn.myanimelist.net/images/anime/1864/96171.webp" },
    { name: "Blue Lock S2", url: "https://cdn.myanimelist.net/images/anime/1056/139398.webp" },
    { name: "Shangri-La Frontier S2", url: "https://cdn.myanimelist.net/images/anime/1062/151911.webp" },
    { name: "Re:Zero S3", url: "https://cdn.myanimelist.net/images/anime/1655/153574.webp" },
    { name: "Bleach: TYBW Part 3", url: "https://cdn.myanimelist.net/images/anime/1908/135431.webp" },
    { name: "DanMachi S5", url: "https://cdn.myanimelist.net/images/anime/1301/141040.webp" },
    { name: "Blue Exorcist: Beyond the Snow", url: "https://cdn.myanimelist.net/images/anime/1085/140228.webp" },
    { name: "One Piece", url: "https://cdn.myanimelist.net/images/anime/1244/138851.webp" },
    { name: "Black Clover Movie", url: "https://cdn.myanimelist.net/images/anime/1344/134812.webp" },
    { name: "Haikyuu!! Final", url: "https://cdn.myanimelist.net/images/anime/1120/141151.webp" },
    { name: "Demon Slayer: Infinity Castle", url: "https://cdn.myanimelist.net/images/anime/1484/144186.webp" },
    { name: "Sakamoto Days", url: "https://cdn.myanimelist.net/images/anime/1922/142981.webp" },
    { name: "Solo Leveling S2", url: "https://cdn.myanimelist.net/images/anime/1864/96171.webp" }
];

let displayedCount = 15;
const grid = document.getElementById('recent-grid');
const btnLoad = document.getElementById('btn-load-recent');

function renderAnimes(startIndex, count) {
    const fragment = document.createDocumentFragment();
    const itemsToShow = allRecentData.slice(startIndex, startIndex + count);

    itemsToShow.forEach((anime, index) => {
        const card = document.createElement('a');
        card.href = "#";
        card.className = "anime-card";
        card.innerHTML = `
                    <span class="ep-badge">EP ${startIndex + index + 1}</span>
                    <img src="${anime.url}" alt="${anime.name}">
                    <div class="anime-card-info"><span class="anime-card-title">${anime.name}</span></div>
                `;
        fragment.appendChild(card);
    });
    grid.appendChild(fragment);

    if (displayedCount >= allRecentData.length) {
        btnLoad.style.display = 'none';
    }
}

const servers = ["SubsPlease", "Anime Fire", "Animes Online NZ", "Go Animes", "Saiko Animes", "AnimesRoll", "Animes Zone", "Hinata Soul", "Anitube VIP", "Animes Online HD", "Animes Online CC", "Anime TV", "Animes Flix", "Animes BR", "Animes Telecine", "Dark Animes", "Anime Q", "Animes Up"];

// Renderização da Lista de Episódios com Accordeon
const epContainer = document.getElementById('sidebar-episodes');
allRecentData.forEach((anime, index) => {
    const wrapper = document.createElement('div');
    wrapper.className = 'download-item-wrapper';

    wrapper.innerHTML = `
                <div class="download-item">
                    <span class="download-name">${anime.name}</span>
                    <button class="btn-download" onclick="toggleAccordion(this)">
                         Episódio: ${index + 1}</button>
                </div>
                <div class="download-options">
                    ${servers.map(s => `<a href="#" class="btn-server">${s}</a>`).join('')}
                </div>
            `;
    epContainer.appendChild(wrapper);
});

function toggleAccordion(btn) {
    const options = btn.parentElement.nextElementSibling;
    btn.classList.toggle('active');
    options.classList.toggle('show');
}

renderAnimes(0, 15);

btnLoad.addEventListener('click', () => {
    renderAnimes(displayedCount, 6);
    displayedCount += 6;
});

const sidebarSearch = document.getElementById('sidebarSearch');
const clearSidebar = document.getElementById('clearSidebar');

// Função para renderizar com filtro
function renderSidebarList(filterText = "") {
    epContainer.innerHTML = "";
    allRecentData.forEach((anime, index) => {
        if (anime.name.toLowerCase().includes(filterText.toLowerCase())) {
            const wrapper = document.createElement('div');
            wrapper.className = 'download-item-wrapper';
            wrapper.innerHTML = `
                        <div class="download-item">
                            <span class="download-name">${anime.name}</span>
                            <button class="btn-download" onclick="toggleAccordion(this)">
                                Episódio: ${index + 1}</button>
                        </div>
                        <div class="download-options">
                            ${servers.map(s => `<a href="#" class="btn-server">${s}</a>`).join('')}
                        </div>
                    `;
            epContainer.appendChild(wrapper);
        }
    });
}

// Evento de digitação
sidebarSearch.addEventListener('input', (e) => {
    const value = e.target.value;
    clearSidebar.style.display = value.length > 0 ? 'block' : 'none';
    renderSidebarList(value);
});

// Evento de clique no ícone de limpar
clearSidebar.addEventListener('click', () => {
    sidebarSearch.value = "";
    clearSidebar.style.display = 'none';
    renderSidebarList("");
    sidebarSearch.focus();
});


// Dados dos Sites
const sitesData = [
    { name: "Animes Achimid", lastCheck: "30/01 22:00", online: true },
    { name: "MyAnimeList", lastCheck: "30/01 21:45", online: true },
    { name: "Crunchyroll", lastCheck: "30/01 21:30", online: true },
    { name: "AnimeFire", lastCheck: "30/01 21:00", online: false },
    { name: "BetterAnime", lastCheck: "30/01 20:30", online: true }
];

const siteContainer = document.getElementById('sidebar-sites');
const siteSearch = document.getElementById('siteSearch');
const clearSiteBtn = document.getElementById('clearSiteSearch');

function renderSites(filter = "") {
    siteContainer.innerHTML = "";
    sitesData.forEach(site => {
        if (site.name.toLowerCase().includes(filter.toLowerCase())) {
            const div = document.createElement('div');
            div.className = 'calendar-item ' + (site.online ? 'released' : '');
            div.innerHTML = `    
                        <span class="cal-name">${site.name}</span>
                        <div class="cal-info">
                            <span class="cal-time" style="font-size: 0.7rem;">${site.lastCheck}</span>
                            ${site.online ? '<span class="check-icon">✓</span>' : '<span style="color:var(--accent-red)">✕</span>'}
                        </div>
                    `;
            siteContainer.appendChild(div);
        }
    });
}

// Lógica de Pesquisa da sessão de Sites
siteSearch.addEventListener('input', (e) => {
    const val = e.target.value;
    clearSiteBtn.style.display = val.length > 0 ? 'block' : 'none';
    renderSites(val);
});

clearSiteBtn.addEventListener('click', () => {
    siteSearch.value = "";
    clearSiteBtn.style.display = 'none';
    renderSites("");
    siteSearch.focus();
});

// Inicializa a lista
renderSites();