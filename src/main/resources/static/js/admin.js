// Painel administrativo (FUNC-04/FUNC-05)

// ===== Sistema de abas =====
(function () {
    const tabs = document.querySelectorAll('.admin-tab');
    if (!tabs.length) return;

    function activate(tabId) {
        tabs.forEach(t => {
            const active = t.dataset.tab === tabId;
            t.setAttribute('aria-selected', active ? 'true' : 'false');
        });
        document.querySelectorAll('.admin-tab-panel').forEach(panel => {
            panel.hidden = panel.id !== 'tab-' + tabId;
        });
        try { sessionStorage.setItem('adminTab', tabId); } catch (_) {}
    }

    tabs.forEach(tab => {
        tab.addEventListener('click', () => activate(tab.dataset.tab));
    });

    // Restaura aba salva, ou usa hash da URL, ou padrão "sites"
    const saved = (function () {
        const hash = location.hash.replace('#', '');
        if (hash && document.getElementById('tab-' + hash)) return hash;
        try { return sessionStorage.getItem('adminTab'); } catch (_) { return null; }
    }());
    activate(saved && document.getElementById('tab-' + saved) ? saved : 'sites');
}());

function apiPost(url) {
    return fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/json' } });
}

function handleAdminAction(btn, promise) {
    btn.disabled = true;
    btn.textContent = '...';
    promise.then(res => {
        if (res.ok) {
            location.reload();
        } else {
            btn.disabled = false;
            btn.textContent = res.status === 403 ? 'Sem permissão' : 'Erro';
        }
    }).catch(() => {
        btn.disabled = false;
        btn.textContent = 'Erro';
    });
}

// ===== Aba Animes: atualizar via Jikan =====
document.querySelectorAll('.js-admin-refresh-anime').forEach(btn => {
    btn.addEventListener('click', () => {
        const slug = encodeURIComponent(btn.dataset.slug);
        const orig = btn.textContent;
        btn.disabled = true;
        btn.textContent = '⏳';
        fetch(`/api/v1/anime/${slug}/refresh`, { method: 'POST' })
            .then(res => {
                btn.textContent = res.ok ? '✓' : '✗';
                if (res.ok) {
                    const row = btn.closest('tr');
                    if (row) row.style.opacity = '0.5';
                    setTimeout(() => location.reload(), 1000);
                } else {
                    btn.disabled = false;
                    setTimeout(() => { btn.textContent = orig; }, 2500);
                }
            }).catch(() => {
                btn.textContent = '✗';
                btn.disabled = false;
                setTimeout(() => { btn.textContent = orig; }, 2500);
            });
    });
});

// ===== Aba Animes: traduzir sinopse =====
document.querySelectorAll('.js-admin-translate-anime').forEach(btn => {
    btn.addEventListener('click', () => {
        const slug = encodeURIComponent(btn.dataset.slug);
        btn.disabled = true;
        btn.textContent = '⏳';
        fetch(`/api/v1/anime/${slug}/translate`, { method: 'POST' })
            .then(res => res.ok ? res.json() : null)
            .then(data => {
                if (data?.synopsisPtBr) {
                    btn.textContent = '✓';
                    setTimeout(() => btn.remove(), 1500);
                } else {
                    btn.textContent = '✗';
                    btn.disabled = false;
                    setTimeout(() => { btn.textContent = '🇧🇷'; }, 2500);
                }
            }).catch(() => {
                btn.textContent = '✗';
                btn.disabled = false;
                setTimeout(() => { btn.textContent = '🇧🇷'; }, 2500);
            });
    });
});

document.querySelectorAll('.js-site-type').forEach(btn => {
    btn.addEventListener('click', () => {
        const name = encodeURIComponent(btn.dataset.name);
        const type = btn.dataset.type;
        handleAdminAction(btn, apiPost(`/api/v1/admin/sites/${name}/type/${type}`));
    });
});

document.querySelectorAll('.js-site-toggle').forEach(btn => {
    btn.addEventListener('click', () => {
        const name = encodeURIComponent(btn.dataset.name);
        const enable = btn.dataset.enable === 'true';
        handleAdminAction(btn, apiPost(`/api/v1/admin/sites/${name}/${enable ? 'enable' : 'disable'}`));
    });
});

document.querySelectorAll('.js-moderate').forEach(btn => {
    btn.addEventListener('click', () => {
        const anime = encodeURIComponent(btn.dataset.anime);
        const comment = encodeURIComponent(btn.dataset.comment);
        const approve = btn.dataset.approve === 'true';
        handleAdminAction(btn, apiPost(`/api/v1/admin/comments/${anime}/${comment}/${approve ? 'approve' : 'reject'}`));
    });
});

const btnRunAll = document.getElementById('btnRunAllExtractions');
if (btnRunAll) {
    btnRunAll.addEventListener('click', () => {
        handleAdminAction(btnRunAll, apiPost('/api/v1/site/integration/extraction/all/run'));
    });
}

document.querySelectorAll('.js-site-run').forEach(btn => {
    btn.addEventListener('click', () => {
        if (btn.classList.contains('running')) return;

        const name = encodeURIComponent(btn.dataset.name);
        const originalText = btn.textContent;

        btn.classList.add('running');
        btn.disabled = true;
        btn.textContent = '⏳ Enviando...';

        fetch(`/api/v1/site/integration/extraction/${name}/run`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        }).then(res => {
            if (res.ok) {
                btn.classList.remove('running');
                btn.classList.add('success');
                btn.textContent = '✓ Enviado';
                setTimeout(() => {
                    btn.classList.remove('success');
                    btn.disabled = false;
                    btn.textContent = originalText;
                }, 3000);
            } else {
                btn.classList.remove('running');
                btn.classList.add('error');
                btn.textContent = res.status === 403 ? '🔒 Sem permissão' : '✗ Erro';
                btn.disabled = false;
                setTimeout(() => {
                    btn.classList.remove('error');
                    btn.textContent = originalText;
                }, 3000);
            }
        }).catch(() => {
            btn.classList.remove('running');
            btn.classList.add('error');
            btn.textContent = '✗ Erro';
            btn.disabled = false;
            setTimeout(() => {
                btn.classList.remove('error');
                btn.textContent = originalText;
            }, 3000);
        });
    });
});
