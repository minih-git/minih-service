package com.minih.wx.controller

import cn.hutool.core.lang.Snowflake
import cn.hutool.core.util.IdUtil
import com.minih.wx.service.ChatGPTService
import org.springframework.cache.CacheManager
import org.springframework.web.bind.annotation.GetMapping
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
class OpenAiController(val chatGPTService: ChatGPTService) {


    @GetMapping("/newChat")
    fun newChat(): String {
        return IdUtil.getSnowflakeNextIdStr()
    }

    fun chat(@RequestParam("message") msg: String, @RequestHeader headers: Map<String, String>): SseEmitter {
        val sseEmitter = SseEmitter()
        val uuid = headers["chatId"]
        if (uuid.isNullOrEmpty()) {
            throw RuntimeException()
        }









        return sseEmitter
    }


}