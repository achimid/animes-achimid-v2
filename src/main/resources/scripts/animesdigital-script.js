const posts = []

const episodes = [...document.querySelectorAll('.itemE')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.title_anime').innerText.replace('(Chinese Audio)','').replace('Dublado','').replace('(JP Audio)','').trim()
    const episode = parseInt($episode.querySelector('.number').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    if (!episode) continue;

    const languages = ['PT-BR']
    const isDub = false
    
    const post = {
        from: "Animes Digital",
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