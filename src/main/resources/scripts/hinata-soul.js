const posts = []

const episodes = [...document.querySelectorAll('.epiContainer')[2].querySelectorAll('.ultimosEpisodiosHomeItem')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.ultimosEpisodiosHomeItemInfosNome').innerText.split(' ep ')[0].split('Dublado')[0].split('Episódio')[0].trim()
    const episode = parseInt($episode.querySelector('.ultimosEpisodiosHomeItemInfosNum').innerText.match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const languages = ['PT-BR']
    const isDub = $episode.querySelector('.ultimosEpisodiosHomeItemInfosNome').innerText.toLowerCase().indexOf('dublado') >= 0

    
    const post = {
        from: "Hinata Soul",
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