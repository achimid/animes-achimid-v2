const posts = []

const episodes = [...document.querySelectorAll('.anime-grid article')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.card-title').innerText.split(' – ')[0]
    const episode = parseInt($episode.querySelector('.card-title').innerText.split(' – ')[1].match(/\d+/g))
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