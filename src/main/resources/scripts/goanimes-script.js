const posts = []

const episodes = [...document.querySelectorAll('.ultimosEpisodiosHomeItem')].slice(0, 20).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.ultimosEpisodiosHomeItemInfosNome').innerText.split(' ep ')[0]
    const episode = parseInt($episode.querySelector('.ultimosEpisodiosHomeItemInfosNum').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    const mirrorOnline = url
    
    const post = {
        from: "Go Animes",
        url,
        title,
        anime,
        episode,
        data: {
            mirrors: [
                {
                    description: "Online",
                    url: mirrorOnline
                }
            ].filter(m => m.url)
        }
    }
    
    console.log(post)
    posts.push(post)

}


posts