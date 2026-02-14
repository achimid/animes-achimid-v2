const posts = []

const episodes = [...document.querySelectorAll('#widget_list_movies_series-4 li')].slice(1).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('.entry-title').innerText
    const episode = parseInt($episode.querySelector('.data span').innerText.split('E')[1].split(' ')[0].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "Animes Up",
        url,
        title,
        anime,
        episode,
    }
    
    console.log(post)
    posts.push(post)
    
}

posts
