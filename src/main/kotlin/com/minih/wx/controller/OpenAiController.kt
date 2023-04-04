package com.minih.wx.controller

import org.springframework.cache.CacheManager
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

/**
 * @author hubin
 * @date 2023/4/3
 * @desc
 */
@RestController
@RequestMapping("/api/openai")
class OpenAiController(val cache: CacheManager) {

    fun chat(@RequestParam("message") msg: String, @RequestHeader headers: Map<String, String>): SseEmitter {
        val sseEmitter = SseEmitter()
        val uuid = headers["uuid"]
        if (uuid!!.isBlank()) {
            throw RuntimeException()
        }
        var cache = cache.getCache("uuid")





        return sseEmitter
    }


}