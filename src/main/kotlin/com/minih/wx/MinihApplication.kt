package com.minih.wx

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class MinihApplication

fun main(args: Array<String>) {
    runApplication<MinihApplication>(*args)
}