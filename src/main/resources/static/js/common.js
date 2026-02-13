// LÓGICA DE PESQUISA (SUGESTÕES MODIFICADA COM OBJETOS)
const animes = [
    { title: "Sousou no Frieren", year: "2023", eps: "28", img: "https://cdn.myanimelist.net/images/anime/1015/138006.jpg" },
    { title: "One Piece", year: "1999", eps: "1100+", img: "https://cdn.myanimelist.net/images/anime/6/73245.jpg" },
    { title: "Attack on Titan", year: "2013", eps: "25", img: "https://cdn.myanimelist.net/images/anime/10/47347.jpg" }
];

const searchInput = document.getElementById('animeSearch');
const resultsBox = document.getElementById('searchResults');

let searchTimeout = null

searchInput.addEventListener('input', () => {
    clearTimeout(searchTimeout)
    searchTimeout = setTimeout(searchAnime, 300)
});

function searchAnime() {
    const query = searchInput.value.toLowerCase();
    resultsBox.innerHTML = '';

    fetch(`/api/v1/animes?query=${query}&pageSize=5`).then(res => res.json()).then(result => {
        if (query.length > 0) {
            const filtered = result.content
            if (filtered.length > 0) {
                resultsBox.style.display = 'flex';
                filtered.forEach(anime => {
                    const item = document.createElement('a');
                    item.classList.add('result-item');
                    item.href = `/anime/${anime.slug}`

                    item.innerHTML = `
                            <img src="${anime.imageUrl}" class="result-img" alt="${anime.name}">
                            <div class="result-info">
                                <span class="result-title">${anime.name}</span>
                                <span class="result-meta">Score: ${anime.score || '0.0'} • Status: ${anime.status}</span>
                            </div>
                        `;

                    item.onclick = () => {
                        searchInput.value = anime.name;
                        resultsBox.style.display = 'none';
                    };
                    resultsBox.appendChild(item);
                });
            } else {
                resultsBox.style.display = 'none';
            }
        } else {
            resultsBox.style.display = 'none';
        }
    })
}

document.addEventListener('click', (e) => {
    if (!e.target.closest('.search-container')) {
        resultsBox.style.display = 'none';
    }
});
