const posts = []

const episodes = [...document.querySelectorAll('article')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.card-vertical-title h3').innerText
    const episode = parseInt($episode.querySelector('.card-vertical-episodes span').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Better Anime",
        url,
        title,
        anime,
        episode,
    }
    
    console.log(post)
    posts.push(post)
    
}

posts
