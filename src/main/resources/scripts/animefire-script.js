const posts = []

const episodes = [...document.querySelectorAll('.container .divCardUltimosEpsHome')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.cardUltimosEps').innerText.split(' - Epi')[0]
    const episode = parseInt($episode.querySelector('.numEp').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Anime Fire",
        url,
        title,
        anime,
        episode,
    }
    console.log(post)
    posts.push(post)        
}

posts