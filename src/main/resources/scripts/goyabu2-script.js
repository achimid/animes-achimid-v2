const posts = []

const episodes = [...document.querySelectorAll('.item.episodes')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.eptitle').innerText.split(' Episodio')[0].trim()
    const episode = parseInt($episode.querySelector('.eptitle').innerText.split(' Episodio')[1].trim().match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const post = {
        from: "Goyabu",
        url,
        title,
        anime,
        episode,
    }

    console.log(post)
    posts.push(post)

}

posts