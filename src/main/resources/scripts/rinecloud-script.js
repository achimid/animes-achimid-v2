const posts = []

const episodes = [...document.querySelectorAll('article .bsx')].slice(0, 12).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.tt').innerText
    const episode = parseInt($episode.querySelector('.epx').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Rine Cloud",
        url,
        title,
        anime,
        episode,
    }
        
    console.log(post)
    posts.push(post)        
}

posts