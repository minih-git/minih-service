package com.minih.wx.service

import cn.hutool.json.JSONUtil
import com.plexpt.chatgpt.ChatGPT
import com.plexpt.chatgpt.entity.chat.ChatCompletion
import com.plexpt.chatgpt.entity.chat.Message
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 * @author hubin
 * @date 2023/4/4
 * @desc
 */
@Service
class ChatGPTService(val redisTemplate: RedisTemplate<String, String>) {

    val apiKey = "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"

    fun simpleChat(user: String?, msg: String?): Message {
        if (user.isNullOrEmpty() || msg.isNullOrEmpty()) {
            return Message.of("请输入问题！")
        }
        val cache = redisTemplate.opsForList().range("chatgpt-chatId:$user", 0, -1)
        val promptList: MutableList<Message> = mutableListOf()
        if (cache.isNullOrEmpty()) {
            val system: Message = Message.ofSystem("你是一个专业的AI助手")
            promptList.add(system)
            redisTemplate.opsForList().rightPush("chatgpt-chatId:$user", JSONUtil.toJsonStr(system))

        } else {
            cache.forEach {
                promptList.add(JSONUtil.toBean(it, Message::class.java))
            }
        }
        val chatGPT = ChatGPT.builder()
            .apiKey(apiKey)
            .timeout(900)
            .apiHost("https://service-ibo78qcu-1256174042.sg.apigw.tencentcs.com/")
            .build()
            .init()
        val message: Message = Message.of(msg)
        promptList.add(message)
        redisTemplate.opsForList().rightPush("chatgpt-chatId:$user", JSONUtil.toJsonStr(message))
        val chatCompletion = ChatCompletion.builder()
            .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
            .messages(promptList)
            .maxTokens(3000)
            .temperature(0.9)
            .build()
        val response = chatGPT.chatCompletion(chatCompletion)
        response.choices?.let {
            val res: Message = response.choices[0].message
            redisTemplate.opsForList().rightPush("chatgpt-chatId:$user", JSONUtil.toJsonStr(res))
            return res;
        }
        redisTemplate.delete("chatgpt-chatId:$user")
        val retryS = redisTemplate.opsForValue()["chatgpt-chatId-retry:$user"]
        var retry = 0
        retryS?.let {
            retry = it.toInt()
            if (retry > 3) {
                return Message.of("机器人出错了，请稍后再试!")
            }
            retry++
        }
        redisTemplate.opsForValue()["chatgpt-chatId-retry:$user", retry.toString(), 1] = TimeUnit.MINUTES
        return simpleChat(user, msg)
    }

}