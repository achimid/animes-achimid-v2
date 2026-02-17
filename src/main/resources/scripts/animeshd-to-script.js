const posts = []

const episodes = [...document.querySelectorAll('.episodes')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.serie').innerText
    const episode = parseInt($episode.querySelector('h3').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const post = {
        from: "Animes HD",
        url,
        title,
        anime,
        episode,
    }
    console.log(post)
    posts.push(post)
}

posts
