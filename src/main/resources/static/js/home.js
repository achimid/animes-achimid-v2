const grid = document.getElementById('recent-grid');
const btnLoad = document.getElementById('btn-load-recent');
const epContainer = document.getElementById('sidebar-episodes');

const sidebarSearch = document.getElementById('sidebarSearch');
const clearSidebar = document.getElementById('clearSidebar');

const siteContainer = document.getElementById('sidebar-sites');
const siteSearch = document.getElementById('siteSearch');
const clearSiteBtn = document.getElementById('clearSiteSearch');


// Dados dos Sites
const sitesData = [
    { name: "Animes Achimid", lastCheck: "30/01 22:00", online: true },
    { name: "MyAnimeList", lastCheck: "30/01 21:45", online: true },
    { name: "Crunchyroll", lastCheck: "30/01 21:30", online: true },
    { name: "AnimeFire", lastCheck: "30/01 21:00", online: false },
    { name: "BetterAnime", lastCheck: "30/01 20:30", online: true }
];



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



btnLoad.addEventListener('click', showMoreButton);

sidebarSearch.addEventListener('keyup', (e) => {
    e.preventDefault();
    if (e.key === "Enter") {
        const value = e.target.value;
        clearSidebar.style.display = value.length > 0 ? 'block' : 'none';
        filterAnimeReleaseEpisode(value);
    }
});

clearSidebar.addEventListener('click', () => {
    sidebarSearch.value = "";
    clearSidebar.style.display = 'none';
    filterAnimeReleaseEpisode("");
    sidebarSearch.focus();
});




function toggleAccordion(btn) {
    const options = btn.parentElement.nextElementSibling;
    btn.classList.toggle('active');
    options.classList.toggle('show');
}




function showMoreButton() {
    fetch("/api/v1/release").then(res => res.json()).then(result => {
        result.releases.forEach((anime, index) => {
            grid.innerHTML += `
                <a class="anime-card" href="/anime/${anime.animeSlug}">
                    <span class="ep-badge">${anime.animeType}: ${anime.animeNumber}</span>
                    <img src="${anime.animeImageUrl}" alt="${anime.animeTitle}">
                    <div class="anime-card-info">
                        <span class="anime-card-title">${anime.animeTitle}</span>
                    </div>
                </a>
            `
        });
    })
}

function filterAnimeReleaseEpisode(query) {
    fetch(`/api/v1/release?query=${query}`).then(res => res.json()).then(result => {
        epContainer.innerHTML = ''
        result.releases.forEach((anime, index) => {
            epContainer.innerHTML += `
                <div class="download-item-wrapper">
                    <div class="download-item">
                        <span class="download-name">${anime.animeTitle}</span>
                        <button class="btn-download" onclick="toggleAccordion(this)">${anime.animeType}: ${anime.animeNumber}</button>
                    </div>
                    <div class="download-options">
                        ${anime.options.map(item => {
                            return `<a class="btn-server" href="${item.url}">${item.name}</a>`    
                        }).join('')}                                                
                    </div>
                </div>
            `
        });
    })
}
