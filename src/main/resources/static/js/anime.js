function toggleEpOptions(element) {
    const options = element.nextElementSibling;
    const isVisible = options.style.display === "flex";
    document.querySelectorAll('.episode-options').forEach(el => el.style.display = 'none');
    options.style.display = isVisible ? "none" : "flex";
}

function toggleFavorite() {
    const btn = document.getElementById('fav-btn');
    if (btn.innerText.includes("+")) {
        btn.innerText = "✓ Na Lista";
        btn.style.borderColor = "var(--primary-green)";
        btn.style.color = "var(--primary-green)";
    } else {
        btn.innerText = "+ Minha Lista";
        btn.style.borderColor = "var(--text-gray)";
        btn.style.color = "var(--text-gray)";
    }
}

// NOVA FUNÇÃO PARA REMOVER COMENTÁRIO
function removeComment(button) {
    if(confirm("Deseja realmente excluir este comentário?")) {
        button.closest('.comment-item').remove();
    }
}