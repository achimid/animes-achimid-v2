const searchField = document.getElementById('animeSearchList');
const sortSelect  = document.getElementById('animeSort');
const genreSelect = document.getElementById('animeGenre');
const animeContainer = document.getElementById('animeContainer');
const paginationEl = document.getElementById('pagination');

const PAGE_SIZE = 24;
let currentPage = 0;
let searchFieldTimeout = null;

function fetchAnimes() {
    const query = searchField.value.trim();
    const sort  = sortSelect.value;
    const genre = genreSelect.value;

    const params = new URLSearchParams({ sort, pageNumber: currentPage, pageSize: PAGE_SIZE });
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

        renderPagination(result);
    });
}

function renderPagination(result) {
    if (!paginationEl) return;

    const page       = result.page || {};
    const totalPages = page.totalPages || 0;
    const number     = page.number     || 0;
    const isFirst    = number === 0;
    const isLast     = number === totalPages - 1;

    if (totalPages === 0) {
        paginationEl.innerHTML = '';
        paginationEl.style.display = 'none';
        return;
    }

    paginationEl.style.display = 'flex';

    const start = Math.max(0, number - 2);
    const end   = Math.min(totalPages - 1, number + 2);

    let html = `<a class="page-item${isFirst ? ' disabled' : ''}" data-page="${number - 1}">&#8249;</a>`;

    for (let i = start; i <= end; i++) {
        html += `<a class="page-item${i === number ? ' active' : ''}" data-page="${i}">${i + 1}</a>`;
    }

    html += `<a class="page-item${isLast ? ' disabled' : ''}" data-page="${number + 1}">&#8250;</a>`;

    paginationEl.innerHTML = html;

    paginationEl.querySelectorAll('.page-item:not(.disabled)').forEach(btn => {
        btn.addEventListener('click', () => {
            currentPage = parseInt(btn.dataset.page);
            fetchAnimes();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    });
}

searchField.addEventListener('keyup', () => {
    clearTimeout(searchFieldTimeout);
    searchFieldTimeout = setTimeout(() => {
        currentPage = 0;
        fetchAnimes();
    }, 300);
});

sortSelect.addEventListener('change', () => { currentPage = 0; fetchAnimes(); });
genreSelect.addEventListener('change', () => { currentPage = 0; fetchAnimes(); });
