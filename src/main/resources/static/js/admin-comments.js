// Página de gerenciamento de comentários (/admin/comments)

let currentStatus = 'all';
let currentSearch = '';
let currentSort = 'date-desc';

function acApiPost(url) {
    return fetch(url, { method: 'POST', headers: { 'Content-Type': 'application/json' } });
}

function applyFilters() {
    const rows = document.querySelectorAll('#acBody .ac-row');
    let visible = 0;

    rows.forEach(function(row) {
        const status = row.getAttribute('data-status') || '';
        const anime = (row.getAttribute('data-anime') || '').toLowerCase();
        const user = (row.getAttribute('data-user') || '').toLowerCase();
        const content = (row.getAttribute('data-content') || '').toLowerCase();
        const q = currentSearch.toLowerCase();

        const matchesStatus = currentStatus === 'all' || status === currentStatus;
        const matchesSearch = !q || anime.indexOf(q) !== -1 || user.indexOf(q) !== -1 || content.indexOf(q) !== -1;

        row.style.display = (matchesStatus && matchesSearch) ? '' : 'none';
        if (matchesStatus && matchesSearch) visible++;
    });

    const empty = document.getElementById('acEmpty');
    if (empty) empty.style.display = visible === 0 ? 'block' : 'none';
}

function applySort() {
    const tbody = document.getElementById('acBody');
    if (!tbody) return;
    const rows = Array.prototype.slice.call(tbody.querySelectorAll('.ac-row'));

    rows.sort(function(a, b) {
        switch (currentSort) {
            case 'date-asc':
                return (a.getAttribute('data-date') || '').localeCompare(b.getAttribute('data-date') || '');
            case 'date-desc':
                return (b.getAttribute('data-date') || '').localeCompare(a.getAttribute('data-date') || '');
            case 'anime':
                return (a.getAttribute('data-anime') || '').localeCompare(b.getAttribute('data-anime') || '');
            case 'user':
                return (a.getAttribute('data-user') || '').localeCompare(b.getAttribute('data-user') || '');
            case 'status': {
                var order = { PENDING: 0, APPROVED: 1, REJECTED: 2 };
                return (order[a.getAttribute('data-status')] || 9) - (order[b.getAttribute('data-status')] || 9);
            }
            default:
                return 0;
        }
    });

    rows.forEach(function(r) { tbody.appendChild(r); });
    applyFilters();
}

function updateSummary() {
    var rows = document.querySelectorAll('#acBody .ac-row');
    var counts = { PENDING: 0, APPROVED: 0, REJECTED: 0 };
    rows.forEach(function(r) {
        var s = r.getAttribute('data-status');
        if (counts[s] !== undefined) counts[s]++;
    });
    var el = document.getElementById('acSummary');
    if (el) {
        el.innerHTML =
            '<span style="color:#ffbe32">' + counts.PENDING + ' pendentes</span> &nbsp;·&nbsp; ' +
            '<span style="color:var(--color-accent)">' + counts.APPROVED + ' aprovados</span> &nbsp;·&nbsp; ' +
            '<span style="color:var(--color-danger)">' + counts.REJECTED + ' rejeitados</span>';
    }
}

// Filtros de status
document.querySelectorAll('.ac-filter-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
        document.querySelectorAll('.ac-filter-btn').forEach(function(b) { b.classList.remove('active'); });
        btn.classList.add('active');
        currentStatus = btn.getAttribute('data-status');
        applyFilters();
    });
});

// Busca
var acSearchInput = document.getElementById('acSearch');
if (acSearchInput) {
    acSearchInput.addEventListener('input', function() {
        currentSearch = acSearchInput.value;
        applyFilters();
    });
}

// Ordenação
var acSortSelect = document.getElementById('acSort');
if (acSortSelect) {
    acSortSelect.addEventListener('change', function() {
        currentSort = acSortSelect.value;
        applySort();
    });
}

// Aprovar / Rejeitar
document.querySelectorAll('.js-ac-moderate').forEach(function(btn) {
    btn.addEventListener('click', function() {
        var anime = encodeURIComponent(btn.getAttribute('data-anime'));
        var comment = encodeURIComponent(btn.getAttribute('data-comment'));
        var approve = btn.getAttribute('data-approve') === 'true';
        btn.disabled = true;
        acApiPost('/api/v1/admin/comments/' + anime + '/' + comment + '/' + (approve ? 'approve' : 'reject'))
            .then(function(res) {
                if (res.ok) { location.reload(); }
                else { btn.disabled = false; }
            })
            .catch(function() { btn.disabled = false; });
    });
});

// Excluir
document.querySelectorAll('.js-ac-delete').forEach(function(btn) {
    btn.addEventListener('click', function() {
        if (!confirm('Deseja excluir permanentemente este comentário?')) return;
        var anime = encodeURIComponent(btn.getAttribute('data-anime'));
        var comment = encodeURIComponent(btn.getAttribute('data-comment'));
        btn.disabled = true;
        btn.textContent = '...';
        fetch('/api/v1/admin/comments/' + anime + '/' + comment, { method: 'DELETE' })
            .then(function(res) {
                if (res.ok) {
                    var row = btn.closest('.ac-row');
                    if (row) row.remove();
                    updateSummary();
                    applyFilters();
                } else {
                    btn.disabled = false;
                }
            })
            .catch(function() { btn.disabled = false; });
    });
});

updateSummary();
applyFilters();
