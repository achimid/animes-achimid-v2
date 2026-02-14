const posts = []

const episodes = [...document.querySelectorAll('.episodes')].slice(0, 30).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('span.serie').innerText
    const episode = parseInt($episode.querySelector('.data h3').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const languages = ['PT-BR']
    const isDub = url.toLowerCase().indexOf('-dub') >= 0

    
    const post = {
        from: "Anime Sync",
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