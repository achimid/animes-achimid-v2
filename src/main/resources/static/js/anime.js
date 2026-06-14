function toggleEpOptions(element) {
    const options = element.nextElementSibling;
    const isVisible = options.style.display === "flex";
    document.querySelectorAll('.episode-options').forEach(el => el.style.display = 'none');
    options.style.display = isVisible ? "none" : "flex";
}

const btnFavorite = document.getElementById('fav-btn');

function toggleFavorite(element, animeId) {

    if (btnFavorite.innerText.includes("+")) {
        addFavorite(animeId)
    } else {
        removeFavorite(animeId)
    }
}

function addFavorite(animeId) {
    fetch(`/api/v1/anime/${animeId}/favorite`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'}
    }).then(res => {
        if (res.status === 201) {
            btnFavorite.innerText = "✓ Na Lista";
            btnFavorite.style.borderColor = "var(--primary-green)";
            btnFavorite.style.color = "var(--primary-green)";
        }
    })
}

function removeFavorite(animeId) {
    fetch(`/api/v1/anime/${animeId}/favorite`, {
        method: 'DELETE',
        headers: {'Content-Type': 'application/json'}
    }).then(res => {
        if (res.status === 204) {
            btnFavorite.innerText = "+ Minha Lista";
            btnFavorite.style.borderColor = "var(--text-gray)";
            btnFavorite.style.color = "var(--text-gray)";
        }
    })
}

// --- INTEGRAÇÃO DE COMENTÁRIOS COM BACKEND ---
const textarea = document.getElementById('txtComment');
const publishBtn = document.getElementById('btnPublishComment');
const commentList = document.getElementById('commentList');

function updateCommentCount() {
    const el = document.getElementById('commentsCount');
    if (!el) return;
    const count = commentList ? commentList.querySelectorAll('.comment-item:not(.comment-pending)').length : 0;
    el.textContent = count + (count === 1 ? ' comentário' : ' comentários');
}

// Contador de caracteres
if (textarea) {
    textarea.addEventListener('input', () => {
        const len = textarea.value.length;
        const counter = document.getElementById('charCount');
        if (counter) {
            counter.textContent = `${len} / 1000`;
            counter.classList.toggle('near-limit', len > 800);
        }
    });
}

function buildCommentItem(comment) {
    const avatar = (comment.avatar || comment.userName || 'AA').substring(0, 2).toUpperCase();
    const today = new Date().toISOString().split('T')[0];
    const item = document.createElement('div');
    item.className = 'comment-item comment-pending';
    item.innerHTML = `
        <div class="comment-avatar">${avatar}</div>
        <div class="comment-content">
            <div class="comment-meta">
                <span class="comment-username">${comment.userName || 'Você'}</span>
                <span class="comment-date">${today}</span>
                <span class="comment-pending-badge">⏳ pendente</span>
            </div>
            <p class="comment-text">${comment.content}</p>
        </div>
    `;
    return item;
}

function onClickPublishComment(element, animeId) {
    const content = textarea.value.trim();
    if (content.length < 3) return;

    const comment = {
        userId: getCookie('user_id'),
        content: content,
    };

    publishBtn.disabled = true;
    publishBtn.innerText = 'Publicar';

    fetch(`/api/v1/anime/${animeId}/comment`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(comment)
    }).then(res => {
        if (res.status !== 201) throw new Error(`HTTP ${res.status}`);
        return res.json();
    }).then(saved => {
        textarea.value = '';
        const counter = document.getElementById('charCount');
        if (counter) counter.textContent = '0 / 1000';

        // Exibir imediatamente com badge "pendente"
        if (commentList) {
            const item = buildCommentItem(saved);
            commentList.insertBefore(item, commentList.firstChild);
        }

        const note = document.getElementById('commentModerationNote');
        if (note) {
            note.style.display = 'block';
            setTimeout(() => { note.style.display = 'none'; }, 5000);
        }

        publishBtn.disabled = false;
        publishBtn.innerText = 'Publicar';
    }).catch(() => {
        publishBtn.disabled = false;
        publishBtn.innerText = 'Tentar novamente';
    });
}

// Excluir comentário (admin)
document.querySelectorAll('.js-delete-comment').forEach(btn => {
    btn.addEventListener('click', () => {
        if (!confirm('Excluir este comentário permanentemente?')) return;
        const anime = encodeURIComponent(btn.dataset.anime);
        const comment = encodeURIComponent(btn.dataset.comment);
        btn.disabled = true;
        fetch(`/api/v1/admin/comments/${anime}/${comment}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    const item = btn.closest('.comment-item');
                    if (item) item.remove();
                    updateCommentCount();
                } else {
                    btn.disabled = false;
                }
            })
            .catch(() => { btn.disabled = false; });
    });
});

updateCommentCount();

function getCookie(name) {
    const nameEQ = name + "=";
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1, c.length);
        }
        if (c.indexOf(nameEQ) === 0) {
            return decodeURIComponent(c.substring(nameEQ.length, c.length));
        }
    }
    return null;
}

let firstEpisode = document.querySelector('.ep-title')
if (firstEpisode != null) firstEpisode.click()

function openImageModal() {
    document.getElementById('imageModal').style.display = 'flex';
}

function closeImageModal() {
    document.getElementById('imageModal').style.display = 'none';
}


function changePoster(newSrc) {
    const mainImg = document.getElementById('main-poster');
    const hero = document.querySelector('.anime-hero');

    if(mainImg) {
        // Aplica um efeito de fade simples
        mainImg.style.opacity = '0.5';

        setTimeout(() => {
            mainImg.src = newSrc;
            if(hero) {
                hero.style.backgroundImage = `linear-gradient(0deg, var(--bg-dark) 0%, rgba(0, 0, 0, 0.85) 100%), url(${newSrc})`;
            }
            mainImg.style.opacity = '1';
        }, 200);

        closeImageModal();
    }
}

// Fechar modal ao clicar fora dele
window.onclick = function(event) {
    const modal = document.getElementById('imageModal');
    if (event.target == modal) {
        closeImageModal();
    }
}

// Alterna a sinopse entre português e inglês (FUNC-09)
function switchSynopsis(btn, lang) {
    const block = btn.closest('.synopsis-block');
    block.querySelectorAll('.synopsis-tab').forEach(t => t.classList.remove('active'));
    btn.classList.add('active');
    block.querySelectorAll('.synopsis-content').forEach(c => {
        c.style.display = c.getAttribute('data-lang') === lang ? 'block' : 'none';
    });
}
// Contagem regressiva ao vivo do widget Próximo Episódio (FUNC-13)
(function () {
    var widget = document.getElementById('nepWidget');
    if (!widget) return;
    var airingAt = parseInt(widget.getAttribute('data-airing'), 10);
    if (!airingAt || isNaN(airingAt)) return;

    var countdownEl = document.getElementById('nepCountdown');
    var footerEl = widget.querySelector('.nep-inline-footer');

    function fmt(secs) {
        if (secs <= 0) return 'agora';
        var d = Math.floor(secs / 86400);
        var h = Math.floor((secs % 86400) / 3600);
        var m = Math.floor((secs % 3600) / 60);
        var s = secs % 60;
        if (d > 0) return d + 'd ' + h + 'h';
        if (h > 0) return h + 'h ' + (m > 0 ? m + 'min' : '');
        if (m > 0) return m + 'min ' + s + 's';
        return s + 's';
    }

    function tick() {
        var now = Math.floor(Date.now() / 1000);
        var diff = airingAt - now;
        if (countdownEl) countdownEl.textContent = fmt(diff);
        if (diff <= 0 && footerEl) footerEl.textContent = 'episódio no ar · via AniList';
    }

    tick();
    setInterval(tick, 1000);
}());
