function getPostInfo($episode) {
    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('h1').innerText.replace('Dublado', '')
    const episode = parseInt($episode.querySelector('span').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    return {
        from: "AnimesRoll",
        url,
        title,
        anime,
        episode,
    }
}


const episodes = [...document.querySelectorAll('html body div#__next div ul:nth-child(3) li')].slice(0, 20).reverse()
const posts = episodes.map(getPostInfo)

posts