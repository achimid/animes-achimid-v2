

function sleep(ms) {
    return new Promise((resolve) => {
        setTimeout(resolve, ms);
    });
}

async function extract() {

    window.scrollTo(0, document.body.scrollHeight);
    await sleep(1000)
    window.scrollTo(0, document.body.scrollHeight);
    await sleep(1000)
    document.querySelector("[class^='release-episodes-section']").scrollIntoView();
    await sleep(1000)

    const posts = []

    const episodes = [...document.querySelector("[class^='release-episodes-section']").querySelectorAll("[class^='release-episode-card-body'")].reverse()
    for (let i = 0; i < episodes.length; i++) {
        const $episode = episodes[i]

        const url = $episode.querySelector('a').href
        const anime = $episode.querySelector("h4").innerText.replace(/\([^()]*\)/g, '')
        const episode = parseInt($episode.querySelector("[class^='release-episode-card-media-type']").innerText.match(/\d+/g))
        const title = `${anime} - Episódio ${episode}`

        if (anime.toUpperCase().indexOf('(DUB)') >= 0) continue;

        const post = {
            from: "Crunchyroll",
            url,
            title,
            anime,
            episode,
        }
        console.log(post)
        posts.push(post)
    }

    return posts
}

extract()