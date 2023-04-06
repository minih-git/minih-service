package com.minih.wx.service

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.TimeUnit


/**
 * @author hubin
 * @date 2023/4/4
 * @desc
 */
@Service
@OptIn(BetaOpenAI::class)
class ChatGPTService(
    val redisTemplate: RedisTemplate<String, String>
) {

    val log: Logger = LoggerFactory.getLogger(ChatGPTService::class.java)
    val openAI =
        OpenAI(OpenAIConfig(token = System.getenv("OPENAI_API_KEY"), host = OpenAIHost("https://openai.minih.cn/")))

    @OptIn(BetaOpenAI::class)
    suspend fun textChat(user: String?, msg: String?): ChatMessage? {
        return textChat(user, msg, null)
    }

    @OptIn(BetaOpenAI::class)
    suspend fun textChat(user: String?, msg: String?, sseEmitter: SseEmitter?): ChatMessage? {
        if (user.isNullOrEmpty() || msg.isNullOrEmpty()) {
            return ChatMessage(role = ChatRole.Assistant, content = "请输入问题！")
        }
        val cache = redisTemplate.opsForList().range("chatgpt-chatId:$user", 0, -1)
        val promptList = cache.run {
            val tmp = (this ?: mutableListOf()).map { Json.decodeFromString<ChatMessage>(it) }
                .toMutableList()
            if (this.isNullOrEmpty()) {
                val system: ChatMessage = ChatMessage(
                    role = ChatRole.System,
                    content = "你是一个专业的AI助手,在你的回答中 不要透露任何关于chatgpt的信息。"
                );
                redisTemplate.opsForList().rightPush("chatgpt-chatId:$user", Json.encodeToString<ChatMessage>(system))
                tmp.add(system)
            }
            tmp.add(ChatMessage(role = ChatRole.User, content = msg))
            redisTemplate.opsForList()
                .rightPush(
                    "chatgpt-chatId:$user",
                    Json.encodeToString<ChatMessage>(ChatMessage(role = ChatRole.User, content = msg))
                )
            tmp
        }
        try {
            if (sseEmitter == null) {
                val res = simpleChat(promptList)
                res.choices[0].let {
                    redisTemplate.opsForList()
                        .rightPush("chatgpt-chatId:$user", Json.encodeToString<ChatMessage>(it.message!!))
                    return it.message;
                }
            } else {
                val res = flowChat(promptList)
                res.collect {
                    it.choices[0].delta?.content?.let { it1 -> sseEmitter.send(it1) }
                }
                res.onCompletion { sseEmitter.complete() }
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            redisTemplate.delete("chatgpt-chatId:$user")
            val retryS = redisTemplate.opsForValue()["chatgpt-chatId-retry:$user"]
            var retry = 0
            retryS?.let {
                retry = it.toInt()
                if (retry >= 3) {
                    return ChatMessage(role = ChatRole.Assistant, "机器人出错了，请稍后再试!")
                }
            }
            retry++
            redisTemplate.opsForValue().set("chatgpt-chatId-retry:$user", retry.toString(), 1, TimeUnit.MINUTES)
        }
        return textChat(user, msg, sseEmitter)
    }

    @OptIn(BetaOpenAI::class)
    suspend fun simpleChat(promptList: MutableList<ChatMessage>): ChatCompletion {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = promptList
        )
        return openAI.chatCompletion(chatCompletionRequest)
    }

    @OptIn(BetaOpenAI::class)
    fun flowChat(promptList: MutableList<ChatMessage>): Flow<ChatCompletionChunk> {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = promptList
        )
        return openAI.chatCompletions(chatCompletionRequest)
    }
}