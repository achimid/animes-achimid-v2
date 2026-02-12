const posts = []

const episodes = [...document.querySelectorAll('tr')].slice(1).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const preTitle = $episode.innerText.split('[Erai-raws]')[1].split('[1080p')[0].trim()
    const url = $episode.querySelector('td:nth-child(2) a').href
    const anime = preTitle.replace('\n','').split('-').slice(0, -1).join('-').trim()
    const episode = parseInt(preTitle.split('-').splice(-1))
    const title = `${anime} - Episódio ${episode}`

    const mirrorTorrent = $episode.querySelector('a:nth-child(2)').href

    
    const post = {
        from: "Erai-raws (Nyaa)",
        url,
        title,
        anime,
        episode,
        data: {
            mirrors: [
                {
                    description: "Torrent (PT-BR)",
                    url: mirrorTorrent
                }
            ]
        }
    }
    console.log(post)
    posts.push(post)        
}

posts