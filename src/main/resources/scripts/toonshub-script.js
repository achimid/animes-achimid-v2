const posts = []

const episodes = [...document.querySelectorAll('tr')].slice(1).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const rawTitle = $episode.innerText.split('[ToonsHub]')[1]
    if (!rawTitle) continue
    const preTitle = rawTitle.split('[')[0].trim()
    const anime = preTitle.split('-').slice(0, -1).join('-').trim()
    const episode = parseInt(preTitle.split('-').splice(-1))
    if (!anime || isNaN(episode)) continue
    const title = `${anime} - Episódio ${episode}`

    const url = $episode.querySelector('td:nth-child(2) a').href
    const mirrorTorrent = $episode.querySelector('a:nth-child(2)').href

    const post = {
        from: "ToonsHub (Nyaa)",
        url,
        title,
        anime,
        episode,
        data: {
            mirrors: [
                {
                    description: "Torrent",
                    url: mirrorTorrent
                }
            ]
        }
    }
    console.log(post)
    posts.push(post)
}

posts
