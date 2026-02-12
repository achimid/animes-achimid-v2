package br.com.achimid.animesachimidv2.configurations

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.DAYS
import java.util.concurrent.TimeUnit.HOURS
import java.util.concurrent.TimeUnit.MINUTES

@Configuration
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(
            listOf(
                buildCache("pageAccessCache", 2, MINUTES, 1),
                buildCache("releasesCache", 1, MINUTES, 100),
                buildCache("animeCache", 5, MINUTES, 200),
                buildCache("animesCache", 5, MINUTES, 20),
                buildCache("animeSearchCache", 5, MINUTES, 50),
                buildCache("recommendationsCache", 2, HOURS, 5),
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
