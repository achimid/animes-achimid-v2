const posts = []

const episodes = [...document.querySelectorAll('article.episodes')].slice(0, 20).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const preTitle = $episode.querySelector('img').alt
    const anime = preTitle.split('Episódio')[0].replace('Dublado', '').replace('Legendado', '').trim()
    const episode = parseInt(preTitle.replace('Episodio','Episódio').split('Episódio')[1].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const languages = ['PT-BR']
    const isDub = preTitle.toLowerCase().indexOf('dublado') >= 0

    
    const post = {
        from: "Animes Online Cloud",
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