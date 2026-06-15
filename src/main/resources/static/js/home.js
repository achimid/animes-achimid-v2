const grid = document.getElementById('recent-grid');
const btnLoad = document.getElementById('btn-load-recent');
const epContainer = document.getElementById('sidebar-episodes');

const sidebarSearch = document.getElementById('sidebarSearch');
const clearSidebar = document.getElementById('clearSidebar');

const siteContainer = document.getElementById('sidebar-sites');
const siteSearch = document.getElementById('siteSearch');
const clearSiteBtn = document.getElementById('clearSiteSearch');


btnLoad.addEventListener('click', showMoreButton);

let searchSideBarTimeout = null
sidebarSearch.addEventListener('keyup', (e) => {
    e.preventDefault();
    clearTimeout(searchSideBarTimeout)
    searchSideBarTimeout = setTimeout(() => {
        const value = e.target.value;
        clearSidebar.style.display = value.length > 0 ? 'block' : 'none';
        filterAnimeReleaseEpisode(value);
    }, 300)
});

clearSidebar.addEventListener('click', () => {
    sidebarSearch.value = "";
    clearSidebar.style.display = 'none';
    filterAnimeReleaseEpisode("");
    sidebarSearch.focus();
});

let searchSiteTimeout = null
siteSearch.addEventListener('keyup', (e) => {
    e.preventDefault();
    clearTimeout(searchSiteTimeout)
    searchSiteTimeout = setTimeout(() => {
        const val = e.target.value;
        clearSiteBtn.style.display = val.length > 0 ? 'block' : 'none';
        filterSites(val);
    }, 300)
});

clearSiteBtn.addEventListener('click', () => {
    siteSearch.value = "";
    clearSiteBtn.style.display = 'none';
    filterSites("");
    siteSearch.focus();
});


function toggleAccordion(btn) {
    const options = btn.parentElement.nextElementSibling;
    btn.classList.toggle('active');
    options.classList.toggle('show');
}

setInterval(autoReload, 1000 * 60 * 5)

function autoReload() {
    grid.innerHTML = ''
    releasesPageNumber = 0
    hasMoreReleases = true
    isLoadingReleases = false

    showMoreButton()

    filterAnimeReleaseEpisode('').then(() => {
        const firstDownload = document.querySelector('.btn-download')
        if (firstDownload) firstDownload.click()
    })
}

const RELEASES_PAGE_SIZE = 20
var releasesPageNumber = 1
var hasMoreReleases = true
var isLoadingReleases = false

function buildReleaseCard(anime) {
    const tpl = document.createElement('template')
    tpl.innerHTML = `
        <a class="anime-card card-enter" href="/anime/${anime.animeSlug}">
            ${anime.animeEpisode == null || anime.animeEpisode === ''
                ? `<span class="ep-badge">${anime.animeType}</span>`
                : `<span class="ep-badge">${anime.animeType}: ${anime.animeEpisode}</span>`}
            <button class="btn-quick-search" onclick="selectAnimeEpisodes(event)">
                <i class="fas fa-search"></i>
            </button>
            ${anime.animeStreamUrl == null ? '' : `
                <button class="btn-quick-play" onclick="event.preventDefault(); window.location.href='${anime.animeStreamUrl}'">
                    <i class="fas fa-play"></i>
                </button>
            `}
            ${window.IS_ADMIN ? `<button class="btn-hide-release" data-release-id="${anime.id}" title="Ocultar lançamento" aria-label="Ocultar lançamento">✕</button>` : ''}
            <img src="${anime.animeImageUrl}" alt="${anime.animeName}" loading="lazy">
            <div class="anime-card-info">
                <span class="anime-card-title">${anime.animeName}</span>
            </div>
        </a>
    `.trim()
    return tpl.content.firstElementChild
}

function showMoreButton() {
    if (isLoadingReleases || !hasMoreReleases) return

    isLoadingReleases = true
    btnLoad.disabled = true
    btnLoad.textContent = 'Carregando...'

    const skeletons = Array.from({length: RELEASES_PAGE_SIZE}, () => {
        const sk = document.createElement('div')
        sk.className = 'anime-card-skeleton'
        grid.appendChild(sk)
        return sk
    })

    fetch(`/api/v1/release?pageNumber=${releasesPageNumber}&pageSize=${RELEASES_PAGE_SIZE}`).then(res => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        return res.json()
    }).then(result => {
        skeletons.forEach(sk => sk.remove())
        result.content.forEach(anime => grid.appendChild(buildReleaseCard(anime)))
        // Spring Boot 4 serializa Page como { content, page: { number, totalPages, ... } }
        const page = result.page || {}
        const currentPage = page.number != null ? page.number : releasesPageNumber
        const totalPages = page.totalPages != null ? page.totalPages : 0
        releasesPageNumber = currentPage + 1
        hasMoreReleases = releasesPageNumber < totalPages
        btnLoad.textContent = hasMoreReleases ? 'Exibir mais' : 'Você chegou ao fim 🎉'
        btnLoad.disabled = !hasMoreReleases
    }).catch(err => {
        console.error('Erro ao carregar mais lançamentos:', err)
        skeletons.forEach(sk => sk.remove())
        btnLoad.textContent = 'Erro ao carregar. Tentar novamente'
        btnLoad.disabled = false
    }).finally(() => {
        isLoadingReleases = false
    })
}

function hideRelease(id, card) {
    fetch(`/api/v1/release/${id}/hide`, { method: 'POST' })
        .then(res => { if (res.ok && card) card.remove() })
        .catch(() => {})
}

grid.addEventListener('click', e => {
    const btn = e.target.closest('.btn-hide-release')
    if (!btn) return
    e.preventDefault()
    e.stopPropagation()
    hideRelease(btn.dataset.releaseId, btn.closest('.anime-card'))
})

function filterAnimeReleaseEpisode(query) {
    return fetch(`/api/v1/release?query=${query}&pageSize=10`).then(res => res.json()).then(result => {
        epContainer.innerHTML = ''
        result.content.forEach((anime, index) => {
            epContainer.innerHTML += `
                <div class="download-item-wrapper">
                    <div class="download-item">
                        <a href="/anime/${anime.animeSlug}">
                            <span class="download-name">${anime.animeName}</span>
                        </a>
                          ${anime.animeEpisode == null || anime.animeEpisode === '' ? `
                            <button class="btn-download" onclick="toggleAccordion(this)">${anime.animeType}</button>  
                          ` : `
                            <button class="btn-download" onclick="toggleAccordion(this)">${anime.animeType}: ${anime.animeEpisode}</button>
                          `}                        
                    </div>
                    <div class="download-options">
                        ${anime.options.map(item => {
                return `<a class="btn-server" href="${item.url}" target="_blank" rel="noopener">${item.name}</a>`
            }).join('')}                                                
                    </div>
                </div>
            `
        });
    })
}


function filterSites(query) {
    fetch(`/api/v1/site/integration?query=${query}`).then(res => res.json()).then(result => {
        siteContainer.innerHTML = ''
        result.sites.forEach((site, index) => {
            siteContainer.innerHTML += `
                <div class="site-item ${site.enabled ? 'released' : ''}">
                    <a class="cal-name" href="${site.url}" target="_blank" rel="noopener">${site.name}</a>
                    <div class="cal-info">
                        ${site.lastExecutionDateWithReleaseSuccessFormatted != null ? `<span class="cal-time" style="font-size: 0.7rem;">${site.lastExecutionDateFormatted}</span>` : ''}
                        ${site.lastExecutionSuccess === true ? `<span class="check-icon" title="${site.lastExecutionDateFormatted}">✓</span>` : ''}
                    </div>
                </div>
            `
        });
    })
}


document.getElementById('btnSendSuggestion').addEventListener('click', function () {
    const input = document.getElementById('siteSuggestion');
    const message = document.getElementById('suggestionMessage');
    const button = this;

    let text = input.value.trim()

    if (text !== "") {
        button.disabled = true;
        button.innerText = "ENVIANDO...";

        sendMessage(text).then(response => {
            if (response.ok) {
                input.value = "";
                message.style.display = "block";

                button.disabled = false;
                button.innerText = "ENVIAR SUGESTÃO";

                setTimeout(() => {
                    message.style.display = "none";
                }, 4000);
            }
        }).catch(error => console.error('Erro na requisição:', error));
    } else {
        input.focus();
        input.style.borderColor = "var(--accent-orange)";
        setTimeout(() => {
            input.style.borderColor = "#333";
        }, 2000);
    }
});

function selectAnimeEpisodes(e) {
    e.preventDefault()

    let animeName = e.target.parentElement.parentElement.querySelector('.anime-card-title').textContent

    sidebarSearch.value = animeName;
    filterAnimeReleaseEpisode(animeName);

    sidebarSearch.focus();
}


function scrollSeason(direction) {
    const el = document.getElementById('season-scroll')
    if (!el) return
    const cardWidth = el.querySelector('.anime-card')?.offsetWidth || 160
    el.scrollBy({ left: direction * (cardWidth + 15) * 3, behavior: 'smooth' })
}

const initialDownload = document.querySelector('.btn-download')
if (initialDownload) toggleAccordion(initialDownload);

// Garante que a página inicia no topo, independente do conteúdo carregado dinamicamente
window.scrollTo({ top: 0, behavior: 'instant' });
