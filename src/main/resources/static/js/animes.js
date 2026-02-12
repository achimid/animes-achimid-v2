const searchField = document.getElementById('animeSearchList');
const animeContainer = document.getElementById('animeContainer');

searchField.addEventListener('keyup', (e) => {
    e.preventDefault();
    if (e.key === "Enter") {
        const value = e.target.value;
        filterAnimes(value);
    }
});


function filterAnimes(query) {
    fetch(`/api/v1/animes?query=${query}`).then(res => res.json()).then(result => {
        animeContainer.innerHTML = ''
        result.content.forEach((anime, index) => {
            animeContainer.innerHTML += `
                <article class="anime-card-compact">
                <a class="card-thumb" href="/anime/${anime.slug}" >
                    <img src="${anime.imageUrl}" alt="${anime.name}">
                </a>
                <div class="card-content">
                    <div class="card-top">
                        <a class="card-title" href="/anime/${anime.slug}" >${anime.name}</a>
                        <div class="card-score">★ ${anime.score}</div>
                    </div>
                    <div class="card-info-grid">
                        ${anime.infoList.map(info => {
                            return `
                                <div class="info-item"">
                                    <strong>${info.infoName}</strong>
                                    <span>${info.infoValue}</span>
                                </div>
                            ` 
                        }).join('')}                        
                    </div>
                    <div class="card-footer-tags">
                        ${anime.tags.map(tag => {
                            return `<span class="mini-tag">${tag}</span>`    
                        }).join('')}                        
                    </div>
                </div>
            </article>
            `
        });
    })
}
