    const posts = []

    const episodes = [...document.querySelectorAll('article.item.se.episodes')].reverse()
    for (let i = 0; i < episodes.length; i++) {
        const $episode = episodes[i]

        const url = $episode.querySelector('a').href
        const anime = $episode.querySelector('.data .serie').innerText
        const episode = parseInt($episode.querySelector('.data h3').innerText.match(/\d+/g))
        const title = `${anime} - Episódio ${episode}`

        if (!episode) continue;
        
        const post = {
            from: "Animes Online NZ",
            url,
            title,
            anime,
            episode,
        }
        console.log(post)
        posts.push(post)        
    }

posts