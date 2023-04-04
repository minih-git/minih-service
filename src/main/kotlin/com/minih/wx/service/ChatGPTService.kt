package com.minih.wx.service

import cn.hutool.json.JSONUtil
import com.minih.wx.component.MsgHandler
import com.plexpt.chatgpt.ChatGPT
import com.plexpt.chatgpt.ChatGPTStream
import com.plexpt.chatgpt.entity.chat.ChatCompletion
import com.plexpt.chatgpt.entity.chat.ChatCompletionResponse
import com.plexpt.chatgpt.entity.chat.Message
import com.plexpt.chatgpt.listener.SseStreamListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer


/**
 * @author hubin
 * @date 2023/4/4
 * @desc
 */
@Service
class ChatGPTService(val redisTemplate: RedisTemplate<String, String>) {

    val log: Logger = LoggerFactory.getLogger(MsgHandler::class.java)

    val apiKey = "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"
    val apiHost = "https://openai.minih.cn/"

    fun textChat(user: String?, msg: String?): Message? {
        return textChat(user, msg, null)
    }
    fun textChat(user: String?, msg: String?, sseEmitter: SseEmitter?): Message? {

        if (user.isNullOrEmpty() || msg.isNullOrEmpty()) {
            return Message.of("请输入问题！")
        }
        val cache = redisTemplate.opsForList().range("chatgpt-chatId:$user", 0, -1)
        val promptList: MutableList<Message> = mutableListOf()
        if (cache.isNullOrEmpty()) {
            val system: Message = Message.ofSystem("你是一个专业的AI助手,在你的回答中 不要透露任何关于chatgpt的信息。")
            promptList.add(system)
            redisTemplate.opsForList().rightPush("chatgpt-chatId:$user", JSONUtil.toJsonStr(system))

        } else {
            cache.forEach {
                promptList.add(JSONUtil.toBean(it, Message::class.java))
            }
        }
        val message: Message = Message.of(msg)
        promptList.add(message)
        redisTemplate.opsForList().rightPush("chatgpt-chatId:$user", JSONUtil.toJsonStr(message))
        try {
            if (sseEmitter == null) {
                val res = simpleChat(promptList)
                res.choices?.let {
                    redisTemplate.opsForList().rightPush("chatgpt-chatId:$user", JSONUtil.toJsonStr(res))
                    return res.choices[0].message;
                }
            } else {
                streamChat(promptList, sseEmitter)
                return null
            }
        } catch (e: Exception) {
            redisTemplate.delete("chatgpt-chatId:$user")
            val retryS = redisTemplate.opsForValue()["chatgpt-chatId-retry:$user"]
            var retry = 0
            retryS?.let {
                retry = it.toInt()
                if (retry >= 3) {
                    return Message.of("机器人出错了，请稍后再试!")
                }
            }
            retry++
            redisTemplate.opsForValue().set("chatgpt-chatId-retry:$user", retry.toString(), 1, TimeUnit.MINUTES)
        }
        return textChat(user, msg, sseEmitter)
    }

    fun simpleChat(promptList: MutableList<Message>): ChatCompletionResponse {
        val chatGPT = ChatGPT.builder()
            .apiKey(apiKey)
            .timeout(900)
            .apiHost(apiHost)
            .build()
            .init()
        val chatCompletion = ChatCompletion.builder()
            .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
            .messages(promptList)
            .maxTokens(3000)
            .temperature(0.9)
            .build()
        return chatGPT.chatCompletion(chatCompletion)
    }


    fun streamChat(promptList: MutableList<Message>, sseEmitter: SseEmitter) {
        val chatGPTStream = ChatGPTStream.builder()
            .timeout(600)
            .apiKey(apiKey)
            .apiHost(apiHost)
            .build()
            .init()
        val listener = SseStreamListener(sseEmitter)
        listener.onComplate = Consumer { _: String? -> }
        chatGPTStream.streamChatCompletion(promptList, listener)
    }

}