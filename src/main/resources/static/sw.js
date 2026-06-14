/* Service Worker — Web Push (VAPID) para Animes Achimid */

self.addEventListener('push', event => {
    let data = {};
    try { data = event.data ? event.data.json() : {}; } catch (_) {}

    const title = data.title || 'Animes Achimid';
    const options = {
        body: data.body || 'Novo episódio disponível!',
        icon: data.icon || '/favicon/favicon-32x32.png',
        badge: '/favicon/favicon-32x32.png',
        image: data.icon || undefined,
        data: { slug: data.slug || '' },
        requireInteraction: false,
        tag: 'anime-episode-' + (data.slug || 'new'),
    };

    event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener('notificationclick', event => {
    event.notification.close();
    const slug = event.notification.data && event.notification.data.slug;
    const url = slug ? '/anime/' + slug : '/';
    event.waitUntil(
        clients.matchAll({ type: 'window', includeUncontrolled: true }).then(windowClients => {
            for (const client of windowClients) {
                if (client.url.includes(url) && 'focus' in client) return client.focus();
            }
            if (clients.openWindow) return clients.openWindow(url);
        })
    );
});
