// LÓGICA DE PESQUISA (SUGESTÕES MODIFICADA COM OBJETOS)
const animes = [
    { title: "Sousou no Frieren", year: "2023", eps: "28", img: "https://cdn.myanimelist.net/images/anime/1015/138006.jpg" },
    { title: "One Piece", year: "1999", eps: "1100+", img: "https://cdn.myanimelist.net/images/anime/6/73245.jpg" },
    { title: "Attack on Titan", year: "2013", eps: "25", img: "https://cdn.myanimelist.net/images/anime/10/47347.jpg" }
];

const searchInput = document.getElementById('animeSearch');
const resultsBox = document.getElementById('searchResults');

searchInput.addEventListener('input', () => {
    const query = searchInput.value.toLowerCase();
    resultsBox.innerHTML = '';

    if (query.length > 0) {
        const filtered = animes.filter(anime => anime.title.toLowerCase().includes(query));

        if (filtered.length > 0) {
            resultsBox.style.display = 'flex';
            filtered.forEach(anime => {
                const item = document.createElement('div');
                item.classList.add('result-item');

                item.innerHTML = `
                            <img src="${anime.img}" class="result-img" alt="${anime.title}">
                            <div class="result-info">
                                <span class="result-title">${anime.title}</span>
                                <span class="result-meta">Ano: ${anime.year} • Episódios: ${anime.eps}</span>
                            </div>
                        `;

                item.onclick = () => {
                    searchInput.value = anime.title;
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
});

document.addEventListener('click', (e) => {
    if (!e.target.closest('.search-container')) {
        resultsBox.style.display = 'none';
    }
});
