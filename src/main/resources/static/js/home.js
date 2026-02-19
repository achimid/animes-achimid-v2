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
    releasesPageNumber = 0
    grid.innerHTML = ''
    showMoreButton()
    releasesPageNumber = 1

    filterAnimeReleaseEpisode('')
}

var releasesPageNumber = 1

function showMoreButton() {
    fetch(`/api/v1/release?pageNumber=${releasesPageNumber}`).then(res => res.json()).then(result => {
        result.content.forEach((anime, index) => {
            grid.innerHTML += `
                <a class="anime-card" href="/anime/${anime.animeSlug}">
                    ${anime.animeEpisode == null || anime.animeEpisode === '' ? `
                        <span class="ep-badge">${anime.animeType}</span>
                    ` : `
                        <span class="ep-badge">${anime.animeType}: ${anime.animeEpisode}</span>
                    `}                    
                    <img src="${anime.animeImageUrl}" alt="${anime.animeName}">
                    <div class="anime-card-info">
                        <span class="anime-card-title">${anime.animeName}</span>
                    </div>
                </a>
            `
        });
        releasesPageNumber = result.number + 1
    })
}

function filterAnimeReleaseEpisode(query) {
    fetch(`/api/v1/release?query=${query}&pageSize=10`).then(res => res.json()).then(result => {
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
                        ${site.lastExecutionDate != null ? `<span class="cal-time" style="font-size: 0.7rem;">${site.lastExecutionDateFormatted}</span>` : ''}
                        ${site.lastExecutionSuccess === true ? '<span class="check-icon">✓</span>' : ''}
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


document.querySelector('.btn-download').click()


