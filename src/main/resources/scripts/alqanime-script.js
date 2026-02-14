const posts = []

const episodes = [...document.querySelectorAll('.styletere')].slice(0, 16).reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.querySelector('a').href
    const anime = $episode.querySelector('a').title.split('Episode')[0].trim()  
    const episode = parseInt($episode.querySelector('a').title.split('Episode')[1].split('Sub')[0].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`

    const languages = ['Indonesio']
    const isDub = false

    
    const post = {
        from: "Alqanime",
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