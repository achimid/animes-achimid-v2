// Painel administrativo (FUNC-04/FUNC-05)

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
