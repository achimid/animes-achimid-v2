const posts = []

const episodes = [...document.querySelectorAll('.episode-grid .col')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.list-caption').innerText.replace(' (Dublado)',' ').replace('\n',' ').replace('\n',' ').split('|')[0].trim()
    const episode = parseInt($episode.querySelector('.list-caption').innerText.replace('\n',' ').split('|')[1].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Sushi Animes",
        url,
        title,
        anime,
        episode,
    }
    
    console.log(post)
    posts.push(post)
    
}

posts
