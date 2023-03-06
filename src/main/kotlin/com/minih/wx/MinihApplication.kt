package com.minih.wx

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MinihApplication

fun main(args: Array<String>) {
    runApplication<MinihApplication>(*args)
}
