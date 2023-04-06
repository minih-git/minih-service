package com.minih.wx.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * @author hubin
 * @date 2023/4/6
 * @desc
 */

@Configuration
class RedisConfiguration {
    @OptIn(InternalSerializationApi::class)
    @Bean
    fun redisTemplate(redisConnectionFactory: LettuceConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        template.connectionFactory = redisConnectionFactory
        template.afterPropertiesSet()
        return template
    }



}