const posts = []

const episodes = [...document.querySelectorAll('.column-ani')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.name-poster > div').innerText
    const episode = parseInt($episode.querySelector('.name-poster > div:nth-child(2)').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Saiko Animes",
        url,
        title,
        anime,
        episode,
    }
    
    console.log(post)
    posts.push(post)
    
}

posts
