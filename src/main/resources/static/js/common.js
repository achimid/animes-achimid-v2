document.querySelectorAll('.anime-tooltip-div').forEach(item => {
    const tooltip = item.querySelector('.anime-tooltip');

    item.addEventListener('mousemove', (e) => {
        tooltip.style.left = (e.clientX + 20) + 'px';
        tooltip.style.top = (e.clientY + 20) + 'px';
    });
});


// LÓGICA DE PESQUISA (SUGESTÕES MODIFICADA COM OBJETOS)
const searchInput = document.getElementById('animeSearch');
const resultsBox = document.getElementById('searchResults');

if (searchInput && resultsBox) {
    let searchTimeout = null

    searchInput.addEventListener('input', () => {
        clearTimeout(searchTimeout)
        searchTimeout = setTimeout(searchAnime, 300)
    });

    function searchAnime() {
        const query = searchInput.value.toLowerCase();
        resultsBox.innerHTML = '';

        fetch(`/api/v1/animes?query=${query}&pageSize=8`).then(res => res.json()).then(result => {
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
}



function sendMessage(mensagem) {
    return fetch('https://telegram-notify-api.achimid.com.br/api/v1/message/send', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            token: '5806553287:AAFtDgYzUWMgJvO-Slotz19GyQEPxYa4SHg',
            id: '128348430',
            text: mensagem
        })
    })
}


/* ===== Consentimento de cookies (FUNC-11) ===== */
(function () {
    const CONSENT_KEY = 'cookie_consent';

    function applyConsent(granted) {
        if (typeof gtag === 'function') {
            gtag('consent', 'update', {
                ad_storage: granted ? 'granted' : 'denied',
                analytics_storage: granted ? 'granted' : 'denied'
            });
        }
    }

    function saveDecision(decision) {
        try { localStorage.setItem(CONSENT_KEY, decision); } catch (e) {}
        applyConsent(decision === 'accepted');
        const banner = document.getElementById('cookie-consent');
        if (banner) banner.remove();
    }

    function buildBanner() {
        const banner = document.createElement('div');
        banner.id = 'cookie-consent';
        banner.className = 'cookie-consent';
        banner.setAttribute('role', 'dialog');
        banner.setAttribute('aria-label', 'Aviso de cookies');
        banner.innerHTML = `
            <div class="cookie-consent-text">
                🍪 Usamos cookies essenciais para o site funcionar e, com a sua permissão, cookies de
                análise para melhorar sua experiência. Veja a <a href="/cookies">Política de Cookies</a>.
            </div>
            <div class="cookie-consent-actions">
                <button type="button" class="btn-cookie btn-cookie-reject" id="cookie-reject">Rejeitar</button>
                <button type="button" class="btn-cookie btn-cookie-accept" id="cookie-accept">Aceitar</button>
            </div>
        `;
        document.body.appendChild(banner);
        document.getElementById('cookie-accept').addEventListener('click', () => saveDecision('accepted'));
        document.getElementById('cookie-reject').addEventListener('click', () => saveDecision('rejected'));
    }

    function init() {
        let decision = null;
        try { decision = localStorage.getItem(CONSENT_KEY); } catch (e) {}
        if (decision === 'accepted') { applyConsent(true); return; }
        if (decision === 'rejected') { applyConsent(false); return; }
        buildBanner();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();


/* ===== Central de notificações in-app (FUNC-07) ===== */
(function () {
    const bell = document.getElementById('notifBell');
    const badge = document.getElementById('notifBadge');
    const dropdown = document.getElementById('notifDropdown');
    const list = document.getElementById('notifList');
    const wrapper = document.getElementById('notifWrapper');
    if (!bell || !badge || !dropdown || !list || !wrapper) return;

    let notifications = [];

    function escapeHtml(value) {
        const div = document.createElement('div');
        div.textContent = value == null ? '' : String(value);
        return div.innerHTML;
    }

    function unreadCount() {
        return notifications.filter(n => !n.read).length;
    }

    function renderBadge() {
        const count = unreadCount();
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.hidden = false;
        } else {
            badge.hidden = true;
        }
    }

    function renderList() {
        if (notifications.length === 0) {
            list.innerHTML = '<div class="notif-empty">Você ainda não tem notificações.<br>Favorite animes para ser avisado de novos episódios.</div>';
            return;
        }
        list.innerHTML = notifications.map(n => `
            <a class="notif-item${n.read ? '' : ' notif-unread'}" href="/anime/${encodeURIComponent(n.animeSlug)}">
                <img src="${escapeHtml(n.animeImageUrl)}" class="notif-thumb" alt=""
                     onerror="this.style.visibility='hidden'">
                <div class="notif-text">
                    <span class="notif-item-title">${escapeHtml(n.animeName)}</span>
                    <span class="notif-item-sub">Novo episódio: ${escapeHtml(n.episode)}</span>
                </div>
            </a>
        `).join('');
    }

    function load() {
        fetch('/api/v1/user/notifications')
            .then(res => res.ok ? res.json() : [])
            .then(data => {
                notifications = Array.isArray(data) ? data : [];
                renderBadge();
                wrapper.hidden = false;
            })
            .catch(() => {});
    }

    function pollCount() {
        fetch('/api/v1/user/notifications/count')
            .then(res => res.ok ? res.json() : null)
            .then(data => {
                if (!data) return;
                const serverUnread = data.unread ?? 0;
                if (serverUnread > unreadCount()) {
                    load();
                } else {
                    badge.textContent = serverUnread > 99 ? '99+' : serverUnread;
                    badge.hidden = serverUnread === 0;
                }
            })
            .catch(() => {});
    }

    function markRead() {
        if (unreadCount() === 0) return;
        fetch('/api/v1/user/notifications/read', { method: 'POST' }).catch(() => {});
        notifications = notifications.map(n => Object.assign({}, n, { read: true }));
        renderBadge();
    }

    function openDropdown() {
        renderList();
        dropdown.hidden = false;
        bell.setAttribute('aria-expanded', 'true');
        markRead();
    }

    function closeDropdown() {
        dropdown.hidden = true;
        bell.setAttribute('aria-expanded', 'false');
    }

    bell.addEventListener('click', (e) => {
        e.stopPropagation();
        if (dropdown.hidden) openDropdown(); else closeDropdown();
    });

    document.addEventListener('click', (e) => {
        if (!wrapper.contains(e.target)) closeDropdown();
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') closeDropdown();
    });

    load();
    setInterval(pollCount, 1000 * 60 * 5);

    /* ===== Web Push opt-in (FUNC-07 fase 2) ===== */
    (function () {
        const footer = document.getElementById('notifPushFooter');
        const btn = document.getElementById('notifPushBtn');
        if (!footer || !btn) return;
        if (!('serviceWorker' in navigator) || !('PushManager' in window)) return;

        const vapidKey = window.VAPID_PUBLIC_KEY;
        if (!vapidKey) return;

        function urlBase64ToUint8Array(base64String) {
            const padding = '='.repeat((4 - base64String.length % 4) % 4);
            const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
            const rawData = atob(base64);
            return Uint8Array.from([...rawData].map(c => c.charCodeAt(0)));
        }

        async function registerAndSubscribe() {
            const reg = await navigator.serviceWorker.register('/sw.js');
            let sub = await reg.pushManager.getSubscription();
            if (sub) return sub;
            sub = await reg.pushManager.subscribe({
                userVisibleOnly: true,
                applicationServerKey: urlBase64ToUint8Array(vapidKey),
            });
            return sub;
        }

        async function sendSubscriptionToServer(sub) {
            const key = sub.getKey('p256dh');
            const auth = sub.getKey('auth');
            await fetch('/api/v1/user/push/subscribe', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    endpoint: sub.endpoint,
                    p256dh: btoa(String.fromCharCode(...new Uint8Array(key))).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, ''),
                    auth: btoa(String.fromCharCode(...new Uint8Array(auth))).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, ''),
                }),
            });
        }

        function showPushButton() {
            if (Notification.permission === 'default') {
                footer.hidden = false;
            }
        }

        btn.addEventListener('click', async () => {
            try {
                const permission = await Notification.requestPermission();
                if (permission !== 'granted') { footer.hidden = true; return; }
                const sub = await registerAndSubscribe();
                await sendSubscriptionToServer(sub);
                footer.hidden = true;
                btn.textContent = '✅ Notificações ativadas!';
            } catch (e) {
                console.warn('Web Push error:', e);
            }
        });

        // Registra o SW em background (sem pedir permissão ainda)
        if (Notification.permission === 'granted') {
            navigator.serviceWorker.register('/sw.js').then(reg =>
                reg.pushManager.getSubscription().then(sub => {
                    if (!sub) return reg.pushManager.subscribe({
                        userVisibleOnly: true,
                        applicationServerKey: urlBase64ToUint8Array(vapidKey),
                    }).then(sendSubscriptionToServer);
                })
            ).catch(() => {});
        } else {
            showPushButton();
        }
    })();
})();
// Hamburger menu (mobile)
(function () {
    var btn = document.getElementById('navHamburger');
    var header = document.getElementById('siteHeader');
    if (!btn || !header) return;

    btn.addEventListener('click', function (e) {
        e.stopPropagation();
        var isOpen = header.classList.toggle('nav-open');
        btn.classList.toggle('is-open', isOpen);
        btn.setAttribute('aria-expanded', isOpen);
        btn.setAttribute('aria-label', isOpen ? 'Fechar menu' : 'Abrir menu');
    });

    document.addEventListener('click', function (e) {
        if (!header.contains(e.target) && header.classList.contains('nav-open')) {
            header.classList.remove('nav-open');
            btn.classList.remove('is-open');
            btn.setAttribute('aria-expanded', 'false');
            btn.setAttribute('aria-label', 'Abrir menu');
        }
    });

    // Fecha o menu ao navegar (links internos)
    document.getElementById('siteNav').querySelectorAll('a').forEach(function (a) {
        a.addEventListener('click', function () {
            header.classList.remove('nav-open');
            btn.classList.remove('is-open');
            btn.setAttribute('aria-expanded', 'false');
        });
    });
}());

// Seletor de tema
(function () {
    var toggle = document.getElementById('themeToggle');
    var dropdown = document.getElementById('themeDropdown');
    var switcher = document.getElementById('themeSwitcher');
    var options = document.querySelectorAll('.theme-option');
    if (!toggle) return;

    function applyTheme(theme) {
        if (theme === 'verde') {
            document.documentElement.removeAttribute('data-theme');
        } else {
            document.documentElement.setAttribute('data-theme', theme);
        }
        options.forEach(function (o) {
            o.classList.toggle('active', o.dataset.theme === theme);
        });
        localStorage.setItem('achimid-theme', theme);
    }

    var saved = localStorage.getItem('achimid-theme') || 'verde';
    options.forEach(function (o) {
        o.classList.toggle('active', o.dataset.theme === saved);
    });

    toggle.addEventListener('click', function (e) {
        e.stopPropagation();
        dropdown.hidden = !dropdown.hidden;
    });

    options.forEach(function (opt) {
        opt.addEventListener('click', function () {
            applyTheme(opt.dataset.theme);
            dropdown.hidden = true;
        });
    });

    document.addEventListener('click', function (e) {
        if (!switcher.contains(e.target)) {
            dropdown.hidden = true;
        }
    });
}());
