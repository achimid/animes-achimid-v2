const posts = []

const episodes = [...document.querySelectorAll('.bixbox')[1].querySelectorAll('article')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.ntitle').innerText
    const episode = parseInt($episode.querySelector('.epsx').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    if (!episode) continue;

    const languages = ['PT-BR']
    const isDub = false
    
    const post = {
        from: "Dark Animes",
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