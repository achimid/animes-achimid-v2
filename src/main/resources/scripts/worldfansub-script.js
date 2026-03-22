const posts = []

const episodes = [...document.querySelectorAll('.anime-grid article')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.card-title').innerText.split(' – ').slice(0, -1).join(' ')
    const episode = parseInt($episode.querySelector('.card-title').innerText.split(' – ').slice(-1).join(' ').match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    if (!episode) continue;

    const languages = ['PT-BR']
    const isDub = $episode.querySelector('.card-title').innerText.indexOf('[DUAL]') >= 0
    
    const post = {
        from: "World Fansub",
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