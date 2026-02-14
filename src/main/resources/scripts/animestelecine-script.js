const posts = []

function getAnime(str) {
    let anime = str.replace(/\d/g, '').replace(/.°/g, '').replace(/.ª/g, '').replace('Temporada', '').trim()
    anime = anime.replace(' -', '').replace('- ', '')    

    return anime
}

async function extract() {
    const episodes = [...document.querySelectorAll(".episode")].reverse()
    for (let i = 0; i < episodes.length; i++) {
        const $episode = episodes[i]

        
        const url = $episode.querySelector(".episode-info-title > a").href
        const anime = getAnime($episode.querySelector(".episode-info-title > a").innerText)
        const episode = parseInt($episode.querySelector(".episode-info-title :nth-child(3)").innerText.match(/\d+/g))
        const title = `${anime} - Episódio ${episode}`
        const mirrorOnline = $episode.querySelector(".episode-figure > a").href
        
        let mirrorMP4
        let mirror720p
        let mirror1080p
        
        const $blue = $episode.querySelector(".episode-info-tabs-item-blue")
        if ($blue) {
            $blue.click()
            await new Promise(r => setTimeout(r, 50))
            mirrorMP4 = [...$episode.querySelectorAll(".episode-info-links > a")].filter(e => e.innerText.trim() == "Drive" || e.innerText.trim() == "Mega" )[0].href        
        }
        
        const $green = $episode.querySelector(".episode-info-tabs-item-green")
        if ($green) {
            $green.click()
            await new Promise(r => setTimeout(r, 50))
            mirror720p = [...$episode.querySelectorAll(".episode-info-links > a")].filter(e => e.innerText.trim() == "Drive" || e.innerText.trim() == "Mega" )[0].href
        }

        const $red = $episode.querySelector(".episode-info-tabs-item-red")
        if ($red) {
            $red.click()
            await new Promise(r => setTimeout(r, 50))
            mirror1080p = [...$episode.querySelectorAll(".episode-info-links > a")].filter(e => e.innerText.trim() == "Drive" || e.innerText.trim() == "Mega" )[0].href
        }
        
        
        const post = {
            from: "Animes Telecine",
            url,
            title,
            anime,
            episode,
            data: {
                mirrors: [
                    {
                        description: "Online",
                        url: mirrorOnline
                    },
                    {
                        description: "1080p",
                        url: mirror1080p
                    },
                    {
                        description: "720p",
                        url: mirror720p
                    },
                    {
                        description: "MP4",
                        url: mirrorMP4
                    }
                ].filter(m => m.url)
            }
        }
        
        console.log(post)
        posts.push(post)
        
    }

    return posts
}

extract()
