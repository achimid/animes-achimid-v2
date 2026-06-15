// Painel de preferência de site de notificação para /favoritos
(function () {
    let activePanel = null;
    let activeBtnEl = null;

    function closePanels() {
        if (activePanel) {
            activePanel.remove();
            activePanel = null;
            activeBtnEl = null;
        }
    }

    function positionPanel(panel, btn) {
        const rect = btn.getBoundingClientRect();
        const scrollY = window.scrollY || document.documentElement.scrollTop;
        const panelW = 220;
        let left = rect.left + window.scrollX;
        // se sair da tela pela direita, alinhar à direita do botão
        if (left + panelW > window.innerWidth - 8) left = rect.right + window.scrollX - panelW;
        if (left < 8) left = 8;
        // posicionar abaixo do botão, ou acima se não couber
        const spaceBelow = window.innerHeight - rect.bottom;
        const panelH = 280; // estimativa
        let top;
        if (spaceBelow >= panelH || spaceBelow >= 160) {
            top = rect.bottom + scrollY + 4;
        } else {
            top = rect.top + scrollY - panelH - 4;
        }
        panel.style.position = 'absolute';
        panel.style.left = left + 'px';
        panel.style.top  = top + 'px';
        panel.style.width = panelW + 'px';
        panel.style.zIndex = '9999';
    }

    function updateBtnAppearance(btn, sites) {
        const labelEl = btn.querySelector('.fav-notify-label');
        if (!sites || sites.length === 0) {
            btn.classList.remove('has-pref');
            btn.title = 'Configurar notificação';
            if (labelEl) labelEl.textContent = 'Qualquer site';
        } else {
            btn.classList.add('has-pref');
            const text = sites.length === 1 ? sites[0] : `${sites.length} sites`;
            btn.title = `Notificar via: ${sites.join(', ')}`;
            if (labelEl) labelEl.textContent = text;
        }
    }

    function buildPanel(btn, animeId, currentSites) {
        const sites = Array.isArray(SITE_LIST) ? SITE_LIST : [];
        const panel = document.createElement('div');
        panel.className = 'fav-notify-panel';

        const title = document.createElement('p');
        title.className = 'fav-notify-panel-title';
        title.textContent = 'Notificar via:';
        panel.appendChild(title);

        if (sites.length === 0) {
            const empty = document.createElement('p');
            empty.className = 'fav-notify-empty';
            empty.textContent = 'Nenhum site disponível.';
            panel.appendChild(empty);
        } else {
            sites.forEach(site => {
                const label = document.createElement('label');
                label.className = 'fav-notify-option';
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
        }

        const footer = document.createElement('div');
        footer.className = 'fav-notify-panel-footer';

        const clearBtn = document.createElement('button');
        clearBtn.className = 'fav-notify-clear';
        clearBtn.textContent = 'Qualquer site';
        clearBtn.addEventListener('click', e => {
            e.stopPropagation();
            panel.querySelectorAll('input[type=checkbox]').forEach(c => { c.checked = false; });
        });

        const saveBtn = document.createElement('button');
        saveBtn.className = 'fav-notify-save';
        saveBtn.textContent = 'Salvar';
        saveBtn.addEventListener('click', e => {
            e.stopPropagation();
            const selected = [...panel.querySelectorAll('input[type=checkbox]:checked')].map(c => c.value);
            saveBtn.disabled = true;
            saveBtn.textContent = '…';
            fetch(`/api/v1/user/notification-preference/${encodeURIComponent(animeId)}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ sites: selected }),
            }).then(res => {
                if (res.ok || res.status === 204) {
                    if (selected.length > 0) NOTIF_PREFS[animeId] = selected;
                    else delete NOTIF_PREFS[animeId];
                    updateBtnAppearance(btn, selected);
                    closePanels();
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

    document.querySelectorAll('.js-fav-notify').forEach(btn => {
        const animeId = btn.dataset.animeId;
        const prefs = NOTIF_PREFS && NOTIF_PREFS[animeId] ? Object.values(NOTIF_PREFS[animeId]) : [];
        updateBtnAppearance(btn, prefs);

        btn.addEventListener('click', e => {
            e.preventDefault();
            e.stopPropagation();
            if (activePanel) {
                const same = activeBtnEl === btn;
                closePanels();
                if (same) return;
            }
            const currentPrefs = NOTIF_PREFS && NOTIF_PREFS[animeId]
                ? Object.values(NOTIF_PREFS[animeId])
                : [];
            const panel = buildPanel(btn, animeId, currentPrefs);
            document.body.appendChild(panel);
            positionPanel(panel, btn);
            activePanel = panel;
            activeBtnEl = btn;
        });
    });

    document.addEventListener('click', e => {
        if (activePanel && !activePanel.contains(e.target) && e.target !== activeBtnEl) {
            closePanels();
        }
    });

    window.addEventListener('scroll', closePanels, { passive: true });
    window.addEventListener('resize', closePanels, { passive: true });
}());
