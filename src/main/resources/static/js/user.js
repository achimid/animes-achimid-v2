// Página do usuário (/user)

// ===== Sistema de abas =====
(function () {
    const tabs = document.querySelectorAll('.user-tab');
    if (!tabs.length) return;

    function activate(tabId) {
        tabs.forEach(t => t.setAttribute('aria-selected', t.dataset.tab === tabId ? 'true' : 'false'));
        document.querySelectorAll('.user-tab-panel').forEach(p => {
            p.hidden = p.id !== 'tab-' + tabId;
        });
        try { sessionStorage.setItem('userTab', tabId); } catch (_) {}
    }

    tabs.forEach(tab => tab.addEventListener('click', () => activate(tab.dataset.tab)));

    const saved = (() => {
        const hash = location.hash.replace('#', '');
        if (hash && document.getElementById('tab-' + hash)) return hash;
        try { return sessionStorage.getItem('userTab'); } catch (_) { return null; }
    })();
    activate(saved && document.getElementById('tab-' + saved) ? saved : 'favoritos');
}());

// ===== Busca em favoritos =====
(function () {
    const input = document.getElementById('favSearch');
    const grid  = document.getElementById('favGrid');
    const empty = document.getElementById('favNoResults');
    if (!input || !grid) return;

    input.addEventListener('input', () => {
        const q = input.value.trim().toLowerCase();
        let visible = 0;
        grid.querySelectorAll('.user-fav-card').forEach(card => {
            const name = (card.dataset.name || '').toLowerCase();
            const show = !q || name.includes(q);
            card.style.display = show ? '' : 'none';
            if (show) visible++;
        });
        if (empty) empty.style.display = visible === 0 ? '' : 'none';
    });
}());

// ===== Remover favorito =====
document.querySelectorAll('.js-fav-remove').forEach(btn => {
    btn.addEventListener('click', e => {
        e.preventDefault();
        e.stopPropagation();
        const id = btn.dataset.id;
        if (!id) return;
        btn.disabled = true;
        btn.textContent = '…';
        fetch(`/api/v1/anime/${encodeURIComponent(id)}/favorite`, { method: 'DELETE' })
            .then(res => {
                if (res.ok || res.status === 204) {
                    const card = btn.closest('.user-fav-card');
                    if (card) {
                        card.style.transition = 'opacity 0.25s, transform 0.25s';
                        card.style.opacity = '0';
                        card.style.transform = 'scale(0.9)';
                        setTimeout(() => card.remove(), 260);
                    }
                    // Atualiza badge da aba
                    const badge = document.querySelector('.user-tab[data-tab="favoritos"] .user-tab-badge');
                    if (badge) badge.textContent = Math.max(0, parseInt(badge.textContent) - 1);
                } else {
                    btn.disabled = false;
                    btn.textContent = '✕';
                }
            })
            .catch(() => { btn.disabled = false; btn.textContent = '✕'; });
    });
});

// ===== Configurações: salvar nome =====
(function () {
    const btn      = document.getElementById('btnSaveSettings');
    const input    = document.getElementById('inputUsername');
    const feedback = document.getElementById('settingsFeedback');
    if (!btn || !input) return;

    btn.addEventListener('click', () => {
        btn.disabled = true;
        btn.textContent = 'Salvando…';
        if (feedback) { feedback.textContent = ''; feedback.classList.remove('visible'); }

        fetch('/api/v1/user/settings', {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: input.value.trim() || null }),
        }).then(res => {
            btn.disabled = false;
            btn.textContent = 'Salvar';
            if (res.ok) {
                if (feedback) { feedback.textContent = '✓ Salvo'; feedback.classList.add('visible'); }
                setTimeout(() => feedback && feedback.classList.remove('visible'), 2500);
            } else {
                if (feedback) { feedback.textContent = '✗ Erro ao salvar'; feedback.classList.add('visible'); }
            }
        }).catch(() => {
            btn.disabled = false;
            btn.textContent = 'Salvar';
            if (feedback) { feedback.textContent = '✗ Erro'; feedback.classList.add('visible'); }
        });
    });
}());

// ===== Painel de preferência de site de notificação =====
(function () {
    const UUID_RE = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
    let activePanel = null;

    function closePanels() {
        if (activePanel) {
            activePanel.remove();
            activePanel = null;
        }
    }

    function buildPanel(btn, animeId, currentSites) {
        const sites = (typeof SITE_LIST !== 'undefined' ? SITE_LIST : []);
        const panel = document.createElement('div');
        panel.className = 'user-fav-notify-panel';

        const title = document.createElement('p');
        title.className = 'user-fav-notify-panel-title';
        title.textContent = 'Notificar via:';
        panel.appendChild(title);

        sites.forEach(site => {
            const label = document.createElement('label');
            label.className = 'user-fav-notify-option';

            const cb = document.createElement('input');
            cb.type = 'checkbox';
            cb.value = site;
            cb.checked = currentSites.includes(site);
            label.appendChild(cb);

            const span = document.createElement('span');
            span.textContent = site;
            label.appendChild(span);

            panel.appendChild(label);
        });

        if (sites.length === 0) {
            const empty = document.createElement('p');
            empty.className = 'user-fav-notify-empty';
            empty.textContent = 'Nenhum site disponível.';
            panel.appendChild(empty);
        }

        const footer = document.createElement('div');
        footer.className = 'user-fav-notify-panel-footer';

        const clearBtn = document.createElement('button');
        clearBtn.className = 'user-fav-notify-clear';
        clearBtn.textContent = 'Qualquer site';
        clearBtn.addEventListener('click', () => {
            panel.querySelectorAll('input[type=checkbox]').forEach(c => { c.checked = false; });
        });

        const saveBtn = document.createElement('button');
        saveBtn.className = 'user-fav-notify-save';
        saveBtn.textContent = 'Salvar';
        saveBtn.addEventListener('click', () => {
            const selected = [...panel.querySelectorAll('input[type=checkbox]:checked')].map(c => c.value);
            saveBtn.disabled = true;
            saveBtn.textContent = '…';
            fetch(`/api/v1/user/notification-preference/${encodeURIComponent(animeId)}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ sites: selected }),
            }).then(res => {
                if (res.ok || res.status === 204) {
                    updateBtnAppearance(btn, selected);
                    closePanels();
                    // Update NOTIF_PREFS in memory
                    if (typeof NOTIF_PREFS !== 'undefined') {
                        if (selected.length > 0) NOTIF_PREFS[animeId] = selected;
                        else delete NOTIF_PREFS[animeId];
                    }
                } else {
                    saveBtn.disabled = false;
                    saveBtn.textContent = 'Salvar';
                }
            }).catch(() => { saveBtn.disabled = false; saveBtn.textContent = 'Salvar'; });
        });

        footer.appendChild(clearBtn);
        footer.appendChild(saveBtn);
        panel.appendChild(footer);

        return panel;
    }

    function updateBtnAppearance(btn, sites) {
        if (!sites || sites.length === 0) {
            btn.classList.remove('has-pref');
            btn.title = 'Configurar notificação';
            btn.dataset.label = '';
        } else {
            btn.classList.add('has-pref');
            const label = sites.length === 1 ? sites[0] : `${sites.length} sites`;
            btn.title = `Notificar via: ${sites.join(', ')}`;
            btn.dataset.label = label;
        }
    }

    document.querySelectorAll('.js-fav-notify').forEach(btn => {
        const animeId = btn.dataset.animeId;
        const prefs = typeof NOTIF_PREFS !== 'undefined' ? (NOTIF_PREFS[animeId] || []) : [];
        updateBtnAppearance(btn, prefs);

        btn.addEventListener('click', e => {
            e.preventDefault();
            e.stopPropagation();
            if (activePanel) {
                const wasThisPanel = activePanel.dataset.animeId === animeId;
                closePanels();
                if (wasThisPanel) return;
            }
            const currentPrefs = typeof NOTIF_PREFS !== 'undefined' ? (NOTIF_PREFS[animeId] || []) : [];
            const panel = buildPanel(btn, animeId, Array.isArray(currentPrefs) ? currentPrefs : Object.values(currentPrefs));
            panel.dataset.animeId = animeId;
            btn.closest('.user-fav-card').appendChild(panel);
            activePanel = panel;
        });
    });

    document.addEventListener('click', e => {
        if (activePanel && !activePanel.contains(e.target) && !e.target.closest('.js-fav-notify')) {
            closePanels();
        }
    });
}());

// ===== ID de usuário: copiar e importar =====
(function () {
    const UUID_RE = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

    function getCookie(name) {
        return document.cookie.split(';').map(c => c.trim()).find(c => c.startsWith(name + '='))?.split('=')[1] ?? null;
    }

    const inputId    = document.getElementById('inputUserId');
    const btnCopy    = document.getElementById('btnCopyId');
    const inputImport = document.getElementById('inputImportId');
    const btnImport  = document.getElementById('btnImportId');
    const feedback   = document.getElementById('importIdFeedback');

    if (inputId) {
        const userId = getCookie('user_id');
        if (userId) inputId.value = userId;
    }

    if (btnCopy && inputId) {
        btnCopy.addEventListener('click', () => {
            const val = inputId.value;
            if (!val) return;
            navigator.clipboard.writeText(val).then(() => {
                const orig = btnCopy.textContent;
                btnCopy.textContent = '✓';
                setTimeout(() => { btnCopy.textContent = orig; }, 1500);
            }).catch(() => {
                inputId.select();
                document.execCommand('copy');
            });
        });
    }

    if (btnImport && inputImport) {
        btnImport.addEventListener('click', () => {
            const val = inputImport.value.trim().toLowerCase();
            if (!UUID_RE.test(val)) {
                if (feedback) { feedback.textContent = '✗ ID inválido (deve ser um UUID)'; feedback.classList.add('visible'); }
                return;
            }
            btnImport.disabled = true;
            btnImport.textContent = '…';
            if (feedback) { feedback.textContent = ''; feedback.classList.remove('visible'); }

            fetch('/api/v1/user/transfer', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId: val }),
            }).then(res => {
                if (res.ok || res.status === 204) {
                    location.reload();
                } else if (res.status === 404) {
                    btnImport.disabled = false;
                    btnImport.textContent = 'Aplicar';
                    if (feedback) { feedback.textContent = '✗ ID não encontrado'; feedback.classList.add('visible'); }
                } else {
                    btnImport.disabled = false;
                    btnImport.textContent = 'Aplicar';
                    if (feedback) { feedback.textContent = '✗ Erro ao importar'; feedback.classList.add('visible'); }
                }
            }).catch(() => {
                btnImport.disabled = false;
                btnImport.textContent = 'Aplicar';
                if (feedback) { feedback.textContent = '✗ Erro de conexão'; feedback.classList.add('visible'); }
            });
        });
    }
}());

// ===== Push toggle =====
(function () {
    const btn = document.getElementById('btnPushToggle');
    if (!btn || !('Notification' in window) || !('serviceWorker' in navigator)) {
        if (btn) { btn.textContent = 'Não suportado'; btn.disabled = true; }
        return;
    }

    function updateState() {
        Notification.requestPermission().then(() => {}).catch(() => {});
        const granted = Notification.permission === 'granted';
        btn.textContent = granted ? 'Desativar' : 'Ativar';
        btn.classList.toggle('active', granted);
    }

    updateState();

    btn.addEventListener('click', () => {
        if (Notification.permission === 'granted') {
            // Redireciona para instruções do browser (não há API web para revogar)
            btn.textContent = 'Desative nas configurações do navegador';
            setTimeout(updateState, 3000);
        } else {
            Notification.requestPermission().then(p => {
                if (p === 'granted' && typeof subscribePush === 'function') subscribePush();
                updateState();
            });
        }
    });
}());
