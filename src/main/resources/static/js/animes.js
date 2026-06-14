const searchField = document.getElementById('animeSearchList');
const sortSelect  = document.getElementById('animeSort');
const genreSelect = document.getElementById('animeGenre');
const animeContainer = document.getElementById('animeContainer');

let searchFieldTimeout = null;

function fetchAnimes() {
    const query = searchField.value.trim();
    const sort  = sortSelect.value;
    const genre = genreSelect.value;

    const params = new URLSearchParams({ sort });
    if (query) params.set('query', query);
    if (genre) params.set('genre', genre);

    fetch(`/api/v1/animes?${params}`).then(res => res.json()).then(result => {
        animeContainer.innerHTML = '';
        result.content.forEach(anime => {
            animeContainer.innerHTML += `
                <article class="anime-card-compact">
                <a class="card-thumb" href="/anime/${anime.slug}">
                    <img src="${anime.imageUrl}" alt="${anime.name}" loading="lazy">
                </a>
                <div class="card-content">
                    <div class="card-top">
                        <a class="card-title" href="/anime/${anime.slug}">${anime.name}</a>
                        <div class="card-score">★ ${anime.score || '?'}</div>
                    </div>
                    <div class="card-info-grid">
                        ${(anime.infoList || []).map(info => `
                            <div class="info-item">
                                <strong>${info.infoName}</strong>
                                <span>${info.infoValue}</span>
                            </div>
                        `).join('')}
                    </div>
                    <div class="card-footer-tags">
                        ${(anime.tags || []).map(tag => `<span class="mini-tag">${tag}</span>`).join('')}
                    </div>
                </div>
                </article>
            `;
        });

        if (result.content.length === 0) {
            animeContainer.innerHTML = '<p style="color:#888; padding:40px; text-align:center;">Nenhum anime encontrado com esses filtros.</p>';
        }
    });
}

searchField.addEventListener('keyup', () => {
    clearTimeout(searchFieldTimeout);
    searchFieldTimeout = setTimeout(fetchAnimes, 300);
});

sortSelect.addEventListener('change', fetchAnimes);
genreSelect.addEventListener('change', fetchAnimes);
