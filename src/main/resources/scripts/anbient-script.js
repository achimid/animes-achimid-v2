function capitalizeWords(str) {
    return str.split(' ').map(element => {
      return element.charAt(0).toUpperCase() + element.slice(1).toLowerCase();
    }).join(' ')
}

function range(rangeArray) {
    const [lowEnd, highEnd] = rangeArray
    const list = []

    for (var i = lowEnd; i <= highEnd; i++) {
        list.push(i)
    }

    return list
}

function getSemiTitle(str) {
    return capitalizeWords(str).replace('(completo)','').replace('(fim)','').trim()
}

function getAnime(str) {    
    
    if (str.indexOf('-') >= 0) {
        return str.split(' ').slice(0, -1).join(' ')
    } else if (str.indexOf(' E ') >= 0) {
        return str.split(' ').slice(0, -3).join(' ')
    }

    return str.split(' ').slice(0, -1).join(' ')
}

function getEpisodeList(str) {
    
    if (str.indexOf('-') >= 0) {
        return range(str.split(' ').slice(-1).join().split('-'))
    } else if (str.indexOf(' e ') >= 0) {
        return range(str.split(' ').slice(-3).filter(s => s != 'E'))
    }

    return str.split(' ').slice(-1)
}

async function extract() {
    const episodes = [...document.querySelectorAll('.lancamento')].reverse()
    for (let i = 0; i < episodes.length; i++) {
        const $episode = episodes[i]

        const semiTitle = getSemiTitle($episode.querySelector('.wrapper').querySelector('.title').text)
        const url = $episode.querySelector('.wrapper').querySelector('a').href
        const anime = getAnime(semiTitle)
        const episodeList = getEpisodeList(semiTitle)
        const mirrorOnline = url
        
        for (let j = 0; j < episodeList.length; j++) {
            const episode = parseInt(episodeList[j])
            const title = `${anime} - Episódio ${episode}`

            const post = {
                from: "Anbient",
                url,
                title,
                anime,
                episode,
                mirrors: [
                    {
                        description: "Online",
                        url: mirrorOnline
                    }
                ].filter(m => m.url)
            }
            
            console.log(post)

            var myHeaders = new Headers();
            myHeaders.append("Content-Type", "application/json")

            var raw = JSON.stringify(post)

            var requestOptions = {
                method: 'POST',
                headers: myHeaders,
                body: raw,
                redirect: 'follow'
            }

            await fetch("https://animes.achimid.com.br/api/v1/integration", requestOptions)
                .then(response => response.text())
                .then(result => console.log(result))
                .catch(error => console.log('error', error))            
        }

        
    }
}

extract()
