const posts = []

const episodes = [...document.querySelectorAll('.listupd.normal .excstf article')].slice(4).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.tt').innerText
    const episode = parseInt($episode.querySelector('.bt').innerText.split(' [')[0].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Ninjin Anime (ESP)",
        url,
        title,
        anime,
        episode,
    }
    console.log(post)
    posts.push(post)        
}

posts