const posts = []

const episodes = [...document.querySelectorAll('.video')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('h2').innerText.split('–')[0].trim()
    const episode = parseInt($episode.querySelector('.video-ep').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const languages = ['PT-BR']
    const isDub = $episode.querySelector('.selo-tipo').innerText.toLowerCase().indexOf('legendado') == -1

    
    const post = {
        from: "Animes Online HD",
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