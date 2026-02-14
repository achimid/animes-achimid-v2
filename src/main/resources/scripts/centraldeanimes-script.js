const posts = []

const episodes = [...document.querySelectorAll('.episodiosItem')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.episodiosTitulo').innerText
    const episode = parseInt($episode.querySelector('.episodiosEpi').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const languages = ['PT-BR']
    const isDub = $episode.querySelector('.episodiosCc').innerText.toLowerCase() !== 'legendado'
    
    const post = {
        from: "Central de Animes",
        url,
        title,
        anime,
        episode,
        languages,
        isDub
    }
    console.log(post)
    posts.push(post)        
}

posts