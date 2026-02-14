const posts = []

const episodes = [...document.querySelectorAll('.left .epi_loop_item')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.epi_loop_nome').innerText.replace(' - Dublado', '').split(' ep ')[0]
    const episode = parseInt($episode.querySelector('.epi_loop_nome_sub').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Anitube VIP",
        url,
        title,
        anime,
        episode,
    }
    console.log(post)
    posts.push(post)        
}

posts
