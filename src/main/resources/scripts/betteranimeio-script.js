const posts = []

const episodes = [...document.querySelectorAll('.animation-2 article')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.info p').innerText
    const episode = parseInt($episode.querySelector('.infoData h3').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Better Anime IO",
        url,
        title,
        anime,
        episode,
    }
    
    console.log(post)
    posts.push(post)
    
}

posts
