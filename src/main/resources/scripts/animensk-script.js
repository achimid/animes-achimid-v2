const posts = []

const episodes = [...document.querySelectorAll('tr')].slice(2, 22).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = window.location.href
    const anime = $episode.querySelector('td:nth-child(1)').innerText.trim()
    const episode = parseInt($episode.querySelector('td:nth-child(2)').innerText.split(' ')[0].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    if (!episode) continue;

    const languages = ['PT-BR']
    const isDub = false
    
    const post = {
        from: "Anime NSK",
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