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

            const tip = document.getElementById('animeFavNotifTip');
            if (tip) tip.style.display = '';

            if ('Notification' in window && Notification.permission === 'default') {
                const prompt = document.getElementById('animeNotifPrompt');
                if (prompt) prompt.style.display = '';
            }
        }
    })
}

// Prompt de notificação ao favoritar
(function () {
    const prompt = document.getElementById('animeNotifPrompt');
    const yesBtn = document.getElementById('animeNotifYes');
    const noBtn  = document.getElementById('animeNotifNo');
    const tip    = document.getElementById('animeFavNotifTip');
    if (!prompt || !yesBtn || !noBtn) return;

    async function trySubscribePush() {
        const vapidKey = window.VAPID_PUBLIC_KEY;
        if (!vapidKey || !('serviceWorker' in navigator) || !('PushManager' in window)) return;
        try {
            const reg = await navigator.serviceWorker.register('/sw.js');
            let sub = await reg.pushManager.getSubscription();
            if (sub) return;
            const padding = '='.repeat((4 - vapidKey.length % 4) % 4);
            const base64 = (vapidKey + padding).replace(/-/g, '+').replace(/_/g, '/');
            const keyBytes = Uint8Array.from([...atob(base64)].map(c => c.charCodeAt(0)));
            sub = await reg.pushManager.subscribe({ userVisibleOnly: true, applicationServerKey: keyBytes });
            const key  = sub.getKey('p256dh');
            const auth = sub.getKey('auth');
            await fetch('/api/v1/user/push/subscribe', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    endpoint: sub.endpoint,
                    p256dh: btoa(String.fromCharCode(...new Uint8Array(key))).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, ''),
                    auth:   btoa(String.fromCharCode(...new Uint8Array(auth))).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, ''),
                }),
            });
        } catch (e) {
            console.warn('Push subscribe error:', e);
        }
    }

    yesBtn.addEventListener('click', async () => {
        yesBtn.disabled = true;
        yesBtn.textContent = '…';
        try {
            const permission = await Notification.requestPermission();
            prompt.style.display = 'none';
            if (permission === 'granted') {
                await trySubscribePush();
                if (tip) {
                    tip.style.display = '';
                    tip.textContent = 'Notificações ativadas ✓';
                    setTimeout(() => { if (tip) tip.textContent = 'Notificações ativas · '; const a = document.createElement('a'); a.href='/favorites'; a.textContent='Gerenciar'; tip.appendChild(a); }, 2000);
                }
            }
        } catch (e) {
            prompt.style.display = 'none';
        }
    });

    noBtn.addEventListener('click', () => { prompt.style.display = 'none'; });
}());

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

// ===== Botão: Traduzir sinopse (admin) =====
const btnTranslate = document.getElementById('btnTranslateSynopsis');
if (btnTranslate) {
    btnTranslate.addEventListener('click', () => {
        btnTranslate.disabled = true;
        btnTranslate.textContent = 'Traduzindo...';
        fetch(`/api/v1/anime/${window.ANIME_SLUG}/translate`, { method: 'POST' })
            .then(r => r.ok ? r.json() : null)
            .then(data => {
                if (!data?.synopsisPtBr) {
                    btnTranslate.textContent = '✗ Erro';
                    btnTranslate.disabled = false;
                    return;
                }
                const ptBlock = document.querySelector('.synopsis-content[data-lang="pt"]');
                const enBlock = document.querySelector('.synopsis-content[data-lang="en"]');
                const tabsBlock = document.querySelector('.synopsis-tabs');
                if (ptBlock) ptBlock.innerHTML = data.synopsisPtBr;
                if (tabsBlock) tabsBlock.style.display = '';
                else if (enBlock) {
                    // Cria as abas dinamicamente
                    const tabs = document.createElement('div');
                    tabs.className = 'synopsis-tabs';
                    tabs.innerHTML = `
                        <button type="button" class="synopsis-tab active" onclick="switchSynopsis(this,'pt')">🇧🇷 Português</button>
                        <button type="button" class="synopsis-tab" onclick="switchSynopsis(this,'en')">🇬🇧 English</button>`;
                    enBlock.parentNode.insertBefore(tabs, enBlock);
                    switchSynopsis(tabs.firstElementChild, 'pt');
                }
                btnTranslate.remove();
            })
            .catch(() => {
                btnTranslate.textContent = '✗ Erro';
                btnTranslate.disabled = false;
            });
    });
}

// ===== Botão: Atualizar dados do anime via Jikan (admin) =====
const btnRefresh = document.querySelector('.js-refresh-anime');
if (btnRefresh) {
    btnRefresh.addEventListener('click', () => {
        if (btnRefresh.disabled) return;
        btnRefresh.disabled = true;
        btnRefresh.textContent = '⏳';
        fetch(`/api/v1/anime/${window.ANIME_SLUG}/refresh`, { method: 'POST' })
            .then(r => {
                if (r.ok) {
                    btnRefresh.textContent = '✓';
                    setTimeout(() => location.reload(), 800);
                } else {
                    btnRefresh.textContent = '✗';
                    btnRefresh.disabled = false;
                    setTimeout(() => { btnRefresh.textContent = '🔄'; }, 2500);
                }
            })
            .catch(() => {
                btnRefresh.textContent = '✗';
                btnRefresh.disabled = false;
                setTimeout(() => { btnRefresh.textContent = '🔄'; }, 2500);
            });
    });
}

function openImageModal() {
    const modal = document.getElementById('imageModal');
    const grid = document.getElementById('imageOptionsGrid');
    modal.style.display = 'flex';
    grid.innerHTML = '<div class="img-option-loading">Carregando opções...</div>';

    fetch(`/api/v1/anime/${window.ANIME_SLUG}/images`)
        .then(res => res.ok ? res.json() : [])
        .then(options => {
            if (!options.length) {
                grid.innerHTML = '<p class="img-option-loading">Nenhuma imagem disponível.</p>';
                return;
            }
            grid.innerHTML = options.map(opt => `
                <div class="img-option">
                    <img src="${opt.url}" onclick="changePoster('${opt.url}')" title="${opt.label}" loading="lazy">
                    <span class="img-option-label">${opt.label}</span>
                </div>
            `).join('');
        })
        .catch(() => {
            grid.innerHTML = '<p class="img-option-loading">Erro ao carregar imagens.</p>';
        });
}

function closeImageModal() {
    document.getElementById('imageModal').style.display = 'none';
}

function changePoster(newSrc) {
    const mainImg = document.getElementById('main-poster');
    const hero = document.querySelector('.anime-hero');
    closeImageModal();
    if (!mainImg) return;

    mainImg.style.opacity = '0.5';
    setTimeout(() => {
        mainImg.src = newSrc;
        if (hero) {
            hero.style.backgroundImage = `linear-gradient(0deg, var(--bg-dark) 0%, rgba(0, 0, 0, 0.85) 100%), url(${newSrc})`;
        }
        mainImg.style.opacity = '1';
    }, 200);

    fetch(`/api/v1/anime/${window.ANIME_SLUG}/image`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ imageUrl: newSrc }),
    }).catch(() => {});
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
