const posts = []

const episodes = [...document.querySelectorAll('#main-content > section .item')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.screen-item-info > a').innerText
    const episode = parseInt($episode.querySelector('small').innerText.split(' - ')[0].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const languages = ['PT-BR']
    const isDub = $episode.querySelector('.tick-item-dub').innerText.toLowerCase().indexOf('dub') >= 0

    
    const post = {
        from: "Animes Vision",
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