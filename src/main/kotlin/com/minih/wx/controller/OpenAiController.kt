package com.minih.wx.controller

import cn.hutool.core.lang.Snowflake
import cn.hutool.core.util.IdUtil
import com.aallam.openai.api.BetaOpenAI
import com.minih.wx.component.SseEmitterMap
import com.minih.wx.service.ChatGPTService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.cache.CacheManager
import org.springframework.web.bind.annotation.CrossOrigin
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
@CrossOrigin
@RequestMapping("/openai")
class OpenAiController(val chatGPTService: ChatGPTService, val cacheManager: CacheManager) {
    @GetMapping("/newChat")
    fun newChat(): String {
        return IdUtil.getSnowflakeNextIdStr()
    }

    @OptIn(BetaOpenAI::class, DelicateCoroutinesApi::class)
    @GetMapping("/chat")
    fun chat(@RequestParam("message") msg: String, @RequestHeader headers: Map<String, String>) {
        val uuid = headers["chat-id"]
        if (uuid.isNullOrEmpty()) {
            throw RuntimeException()
        }
        GlobalScope.launch {
            chatGPTService.textChat(uuid, msg, SseEmitterMap[uuid]);
        }
    }


}