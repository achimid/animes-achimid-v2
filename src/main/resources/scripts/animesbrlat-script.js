const posts = []

const episodes = [...document.querySelectorAll('.MuiButtonBase-root.MuiCardActionArea-root')].reverse()
for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const url = $episode.href
    const anime = $episode.querySelector('.MuiTypography-root.MuiTypography-subtitle1').innerText
    const episode = parseInt($episode.querySelector('.MuiTypography-root.MuiTypography-body2').innerText.split('Episódio')[1].match(/\d+/g))
    const title = `${anime} - Episódio ${episode}`
    
    const post = {
        from: "AnimesBR Lat",
        url,
        title,
        anime,
        episode,
    }
    
    console.log(post)
    posts.push(post)
    
}

posts
