const posts = []

const episodes = [...document.querySelectorAll('.listupd .bsx')].reverse().slice(5, -5)
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.tt').innerText
    const episode = parseInt($episode.querySelector('.epx').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    if (!episode) continue;

    const languages = ['PT-BR']
    const isDub = false
    
    const post = {
        from: "AnimeFlix (ENG)",
        url,
        title,
        anime,
        episode,
        languages,
        isDub
    }
    
    console.log(post)
    posts.push(post) 
    
}

posts