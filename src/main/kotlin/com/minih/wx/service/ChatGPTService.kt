package com.minih.wx.service

import cn.hutool.json.JSONUtil
import com.plexpt.chatgpt.ChatGPT
import com.plexpt.chatgpt.entity.chat.ChatCompletion
import com.plexpt.chatgpt.entity.chat.Message
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

/**
 * @author hubin
 * @date 2023/4/4
 * @desc
 */
@Service
class ChatGPTService(val redisTemplate: RedisTemplate<String, String>) {

    fun simpleChat(user: String, msg: String): Message {
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
            .apiKey("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")
            .timeout(900)
            .apiHost("https://service-ibo78qcu-1256174042.sg.apigw.tencentcs.com/") //反向代理地址
            .build()
            .init()
        val message: Message = Message.of(msg)
        redisTemplate.opsForList().rightPush("chatgpt-chatId:$user", JSONUtil.toJsonStr(message))
        val chatCompletion = ChatCompletion.builder()
            .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
            .messages(promptList)
            .maxTokens(3000)
            .temperature(0.9)
            .build()
        val response = chatGPT.chatCompletion(chatCompletion)
        response.choices?.let {

        }
        val res: Message = response.choices[0].message
        redisTemplate.opsForList().rightPush("chatgpt-chatId:user", JSONUtil.toJsonStr(res))
        return res;
    }

}