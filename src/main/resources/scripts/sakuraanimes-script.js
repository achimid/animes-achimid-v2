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
    return capitalizeWords(str.replace('(Completo)', '').replace('(Blu-Ray)','').replace('©','').replace('+ Aviso',''))
}

function getAnime(str) {    
    if (str.indexOf('- Episódio') > 0) {
        return str.split('- Episódio')[0]
    }
    if (str.indexOf('Episódio') > 0) {
        return str.split('Episódio')[0]
    }
    if (str.indexOf('Episódios') > 0) {
        return str.split('Episódios')[0]
    }
    if (str.indexOf(' Ep ') > 0) {
        return str.split(' Ep ')[0]
    }
    if (str.indexOf(' Eps ') > 0) {
        return str.split(' Eps ')[0]
    }
    if (str.indexOf(' - ') > 0) {
        return str.split(' - ')[0]
    }
    if (str.indexOf(', ') > 0) {
        return str.split(', ')[0].split(' ').slice(0, -1).join(' ')
    }
    if (str.indexOf(' E ') > 0) {
        return str.split(' E ')[0].split(' ').slice(0, -1).join(' ')
    }

    return str.split(' ').slice(0, -1).join(' ')
}

function getEpisodeList(str) {
    let subStr = ''
    if (str.indexOf('- Episódio') > 0) {
        subStr = str.split('- Episódio')[1]
    } else if (str.indexOf('Episódio') > 0) {
        subStr = str.split('Episódio')[1]
    } else if (str.indexOf('Episódios') > 0) {
        subStr = str.split('Episódios')[1]
    } else if (str.indexOf(' Ep ') > 0) {
        subStr = str.split(' Ep ')[1]
    } else if (str.indexOf(' Eps ') > 0) {
        subStr = str.split(' Eps ')[1]    
    } else if (str.indexOf(' - ') > 0) {
        subStr = str.split(' - ')[1]
    } else {
        subStr = str.split(' ').slice(-1)
    }

    
    if (subStr.indexOf(', ') > 0) {
        return subStr.split(', ')
    } else if (subStr.indexOf(',') > 0) {
        return subStr.split(',')
    } else if (subStr.indexOf(' E ') > 0) {
        return subStr.split(' ').slice(-3).filter(s => s != 'E')
    } else if (subStr.indexOf(' Ao ') > 0) {
        return range(subStr.split(' Ao '))
    }

    if (!/\d/.test(subStr)){
        return []
    }

    return [subStr]
}

async function extract() {
    const episodes = [...document.querySelectorAll('.project-name')].reverse()
    for (let i = 0; i < episodes.length; i++) {
        const $episode = episodes[i]

        const semiTitle = getSemiTitle($episode.innerText).trim()
        const url = $episode.querySelector('a').href
        const anime = getAnime(semiTitle).trim()
        const episodeList = getEpisodeList(semiTitle)
        
        for (let j = 0; j < episodeList.length; j++) {
            const episode = parseInt(episodeList[j])
            const title = `${anime} - Episódio ${episode}`

            const post = {
                from: "Sakura Animes",
                url,
                title,
                anime,
                episode
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
