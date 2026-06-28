const NOISE_PATTERN = /\s+(1080p|720p|480p|4K|2160p|WEB-DL|WEBRip|BluRay|Blu-Ray|BDREMUX|BD|HDTV).*$/i

function qualityScore(rawTitle) {
    let score = 0
    if (/multi.?sub/i.test(rawTitle))  score += 10
    if (/1080p/i.test(rawTitle))        score += 5
    else if (/720p/i.test(rawTitle))    score += 2
    return score
}

// Mapa de deduplicação: chave "animeName|episode" → { post, score }
const best = new Map()

const episodes = [...document.querySelectorAll('tr')].slice(1).reverse()

for (let i = 0; i < episodes.length; i++) {
    const $episode = episodes[i]

    const rawTitle = $episode.innerText.split('[ToonsHub]')[1]
    if (!rawTitle) continue

    const score = qualityScore(rawTitle)

    // Remove bracket tags como [CR], [Multi-Subs], etc.
    let clean = rawTitle.replace(/\[[^\]]*\]/g, '').trim()

    // Remove tags de qualidade/codec/fonte e tudo que vem depois
    clean = clean.replace(NOISE_PATTERN, '').trim()

    // Remove sufixos entre parênteses como (English-Sub), (Japanese Sub), (Dual-Audio)
    clean = clean.replace(/\s*\([^)]*\)\s*$/, '').trim()

    let animeName, episode

    // Formato SxxExx → ex: S02E12
    const seasonEpMatch = clean.match(/\bS(\d{1,2})E(\d{1,3})\b/i)
    // Formato EPxxxx → ex: EP1168
    const epOnlyMatch = clean.match(/\bEP(\d+)\b/i)

    if (seasonEpMatch) {
        const season = parseInt(seasonEpMatch[1])
        episode = parseInt(seasonEpMatch[2])
        animeName = clean.substring(0, seasonEpMatch.index).replace(/[-\s]+$/, '').trim()
        if (season > 1) animeName = `${animeName} Season ${season}`
    } else if (epOnlyMatch) {
        episode = parseInt(epOnlyMatch[1])
        animeName = clean.substring(0, epOnlyMatch.index).replace(/[-\s]+$/, '').trim()
    } else {
        // Fallback: último segmento após traço é o episódio
        const parts = clean.split('-')
        episode = parseInt(parts[parts.length - 1].trim())
        animeName = parts.slice(0, -1).join('-').trim()
    }

    if (!animeName || isNaN(episode)) continue

    const title = `${animeName} - Episódio ${episode}`
    const url = $episode.querySelector('td:nth-child(2) a').href
    const mirrorTorrent = $episode.querySelector('a:nth-child(2)').href

    const post = {
        from: "ToonsHub (Nyaa)",
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
