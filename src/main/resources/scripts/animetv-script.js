function getPostInfo($episode) {
    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.tt').innerText
    const episode = parseInt($episode.querySelector('.bt').innerText.split(' [')[0].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    return {
        from: "Anime TV",
        url,
        title,
        anime,
        episode,
    }
}


const episodes = [...document.querySelectorAll('.listupd.normal .excstf article')].slice(6).reverse()
const posts = episodes.map(getPostInfo)

posts