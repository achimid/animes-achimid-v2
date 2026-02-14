function getPostInfo($episode) {
    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.tituloEP').innerText.split(' Epis')[0]
    const episode = parseInt($episode.querySelector('.tituloEP').innerText.split(' Epis')[1].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    return {
        from: "Animes Games",
        url,
        title,
        anime,
        episode,
    }
}

const episodes = [...document.querySelectorAll('.episodioItem')].reverse()
const posts = episodes.map(getPostInfo)
    
posts