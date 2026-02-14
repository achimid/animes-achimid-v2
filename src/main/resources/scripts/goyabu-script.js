const posts = []

const episodes = [...document.querySelectorAll('article')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.title').innerText
    const episode = parseInt($episode.querySelector('.titleEP').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const languages = ['PT-BR']
    const isDub = $episode.getAttribute('data-tar').toLowerCase().indexOf('legendado') == -1

    
    const post = {
        from: "Goyabu",
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