function getPostInfo($episode) {
    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.entry-title').innerText.indexOf('(') >= 0 ? $episode.querySelector('.entry-title').innerText.split('(')[1].split(')')[0] :  $episode.querySelector('.entry-title').innerText
    const episode = parseInt($episode.querySelector('.num-epi').innerText.split('x')[1].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    return {
        from: "Animes Flix",
        url,
        title,
        anime,
        episode,
    }
}

const episodes = [...document.querySelectorAll('article.episodes')].reverse()
const posts = episodes.map(getPostInfo)

posts
        