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

// NOVA FUNÇÃO PARA REMOVER COMENTÁRIO
function removeComment(button) {
    if(confirm("Deseja realmente excluir este comentário?")) {
        button.closest('.comment-item').remove();
    }
}


// --- INTEGRAÇÃO DE COMENTÁRIOS COM BACKEND ---
const textarea = document.getElementById('txtComment');
const publishBtn = document.getElementById('btnPublishComment');
const commentList = document.getElementById('commentList');

function onClickPublishComment(element, animeId) {
    let content = textarea.value.trim()

    if (content.length <= 5) return

    const comment = {
        userId:  getCookie('user_id'),
        content: content,
    };

    publishBtn.disabled = true;
    publishBtn.innerText = "Enviando...";

    fetch(`/api/v1/anime/${animeId}/comment`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(comment)
    }).then(res => {
        if (res.status === 201) return res.json()
    }).then((comment) => {
        textarea.value = '';
        commentList.innerHTML = `
            <div class="comment-item">
                <div class="comment-avatar">${comment.avatar}</div>
                <div class="comment-content">
                    <span class="user-name">${comment.userName}</span>
                    <p class="comment-text">${comment.content}</p>
                    <span class="comment-date">${comment.createdAt.split('T')[0]}</span>
                </div>
            </div>
        ` + commentList.innerHTML;

        publishBtn.disabled = false;
        publishBtn.innerText = "Publicar";
    })


}

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