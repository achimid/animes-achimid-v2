const posts = []

const episodes = [...document.querySelectorAll('.animation-2 .item')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.data h3').innerText.trim()
    const episode = parseInt($episode.querySelector('.epi').innerText.trim().match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Animes House",
        url,
        title,
        anime,
        episode
    }
       
    posts.push(post)        
}

posts