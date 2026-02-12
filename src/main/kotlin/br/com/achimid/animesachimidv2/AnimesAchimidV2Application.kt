package br.com.achimid.animesachimidv2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@EnableCaching
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
class AnimesAchimidV2Application

fun main(args: Array<String>) {
    runApplication<AnimesAchimidV2Application>(*args)
}
