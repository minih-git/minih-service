package com.minih.wx

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession

@SpringBootApplication
@EnableCaching
@EnableRedisHttpSession
class MinihApplication

fun main(args: Array<String>) {
    runApplication<MinihApplication>(*args)
}