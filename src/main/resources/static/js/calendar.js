function renderDay(day) {
    fetch(`/api/v1/calendar?dayIndex=${day}`).then(res => res.json()).then(results => {
        console.log(results)

        const container = document.getElementById('scheduleList');

        container.style.opacity = "0";
        container.innerHTML = "";

        if (results.length === 0) {
            container.innerHTML = `
                <div style="text-align:center; padding:60px 20px; color:#555;">
                    <p style="font-size: 1.2rem;">Sem lançamentos previstos para este dia.</p>
                </div>`
        } else {
            results.forEach(item => {
                container.innerHTML += `
                    <a href="/anime/${item.anime.slug}" class="list-item">
                        <img src="${item.imageUrl}" class="list-img" alt="${item.title}">
                        <div class="list-content">
                            <span class="list-time">${item.time} ${item.released ? `✓` : ``}</span>
                            <span class="list-title">${item.title}</span>
                            <span class="tag-epi">★ ${item.anime.score}</span>
                            <div class="list-meta">
                                <span> • ${item.anime.tags.join(', ')}</span><span></span>
                            </div>
                            <div class="list-tags">
                                <span class="tag-epi">Novo Episódio</span>
                                <span class="tag-genre">${item.anime.status}</span>
                            </div>
                        </div>
                    </a>
                `;
            });
        }
        container.style.opacity = "1";
    })
}

/**
 * Inicialização dos eventos
 */
document.addEventListener("DOMContentLoaded", () => {
    // Configura os cliques nos chips de dias
    const chips = document.querySelectorAll('.chip');
    chips.forEach(btn => {
        btn.addEventListener('click', function() {
            chips.forEach(c => c.classList.remove('active'));
            this.classList.add('active');
            renderDay(this.dataset.day)
        });
    });

    const today = new Date().getDay()-1;
    const activeChip = document.querySelector(`.chip[data-day="${today}"]`);
    if (activeChip) {
        activeChip.click();
    }
});