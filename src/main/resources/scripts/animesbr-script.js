const posts = []

const episodes = [...document.querySelectorAll('.item.episodes')].slice(0, 20).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.data .serie').innerText
    const episode = parseInt($episode.querySelector('.data span').innerText.split('/')[0].trim().split(' ')[1].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const languages = ['PT-BR']
    const isDub = false

    
    const post = {
        from: "Animes BR",
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