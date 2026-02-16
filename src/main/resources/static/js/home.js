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


var releasesPageNumber = 2

function showMoreButton() {
    fetch(`/api/v1/release?pageNumber=${releasesPageNumber}`).then(res => res.json()).then(result => {
        result.content.forEach((anime, index) => {
            grid.innerHTML += `
                <a class="anime-card" href="/anime/${anime.animeSlug}">
                    <span class="ep-badge">${anime.animeType}: ${anime.animeEpisode}</span>
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
    fetch(`/api/v1/release?query=${query}`).then(res => res.json()).then(result => {
        epContainer.innerHTML = ''
        result.content.forEach((anime, index) => {
            epContainer.innerHTML += `
                <div class="download-item-wrapper">
                    <div class="download-item">
                        <a href="/anime/${anime.animeSlug}">
                            <span class="download-name">${anime.animeTitle}</span>
                        </a>
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


function filterSites(query) {
    fetch(`/api/v1/site/integration?query=${query}`).then(res => res.json()).then(result => {
        siteContainer.innerHTML = ''
        result.sites.forEach((site, index) => {
            siteContainer.innerHTML += `
                <div class="calendar-item ${site.enabled ? 'released' : ''}">
                    <a class="cal-name" href="${site.url}">${site.name}</a>
                    <div class="cal-info">
                        ${site.lastExecutionDate != null ? `<span class="cal-time" style="font-size: 0.7rem;">${site.lastExecutionDate}</span>` : ''}
                        ${site.lastExecutionSuccess === true ? '<span class="check-icon">✓</span>' : ''}
                    </div>
                </div>
            `
        });
    })
}