// Padrão Erai-raws: [Erai-raws] Título - Episódio [Resolução Fonte Codec Áudio][Subs][Hash]

function qualityScore(rawTitle) {
    let score = 0
    if (/multi.?sub/i.test(rawTitle))         score += 10
    if (/1080p/i.test(rawTitle))               score += 5
    else if (/720p/i.test(rawTitle))           score += 2
    else if (/480p/i.test(rawTitle))           score += 1
    if (/\bavc\b/i.test(rawTitle))             score += 3  // AVC preferido sobre HEVC
    return score
}

// Mapa de deduplicação: chave "animeName|episode" → { post, score }
const best = new Map()

const episodes = [...document.querySelectorAll('tr')].slice(1).reverse()

for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const rawTitle = $episode.innerText.split('[Erai-raws]')[1]
    if (!rawTitle) continue

    const score = qualityScore(rawTitle)

    // Extrai só a parte "Título - Episódio" antes dos colchetes de qualidade
    const preTitle = rawTitle.split('[')[0].replace(/\n/g, '').trim()

    // Divide pelo último " - " para separar nome do episódio
    const lastDash = preTitle.lastIndexOf(' - ')
    if (lastDash === -1) continue

    const animeName = preTitle.substring(0, lastDash).trim()
    const episode = parseInt(preTitle.substring(lastDash + 3).trim())

    if (!animeName || isNaN(episode)) continue

    const title = `${animeName} - Episódio ${episode}`
    const url = $episode.querySelector('td:nth-child(2) a').href
    const mirrorTorrent = $episode.querySelector('a:nth-child(2)').href

    const post = {
        from: "Erai-raws (Nyaa)",
        url,
        title,
        anime: animeName,
        episode,
        data: {
            mirrors: [
                {
                    description: "Torrent",
                    url: mirrorTorrent
                }
            ]
        }
    }

    const key = `${animeName}|${episode}`
    const existing = best.get(key)
    if (!existing || score > existing.score) {
        best.set(key, { post, score })
    }
}

const posts = [...best.values()].map(({ post }) => {
    console.log(post)
    return post
})

posts
