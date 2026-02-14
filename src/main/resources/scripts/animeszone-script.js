const posts = []

const episodes = [...document.querySelectorAll('.epiItem')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.epiAniTitulo').innerText
    const episode = parseInt([...$episode.querySelectorAll('.epiTipo')].map(e => e.innerText).join(' ').match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Animes Zone",
        url,
        title,
        anime,
        episode,
    }
    console.log(post)
    posts.push(post)        
}

posts