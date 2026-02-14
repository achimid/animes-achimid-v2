package br.com.achimid.animesachimidv2.configurations

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*

@Configuration
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(
            listOf(
                buildCache("pageAccessCache", 5, MINUTES, 1),
                buildCache("releasesCache", 5, MINUTES, 100),
                buildCache("animeCache", 10, MINUTES, 200),
                buildCache("animesCache", 5, MINUTES, 20),
                buildCache("animeSearchCache", 5, MINUTES, 50),
                buildCache("recommendationsCache", 2, HOURS, 5),
                buildCache("scheduleCache", 1, DAYS, 1),
                buildCache("sitesIntegrationCache", 2, MINUTES, 100),

            )
        )
        return cacheManager
    }

    private fun buildCache(name: String, duration: Long, unit: TimeUnit, maximumSize: Long = 1000): CaffeineCache {
        return CaffeineCache(
            name,
            Caffeine.newBuilder()
                .expireAfterWrite(duration, unit)
                .maximumSize(maximumSize)
                .recordStats()
                .build()
        )
    }

}
