package com.minih.wx.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 * @author hubin
 * @date 2023/3/8
 * @desc
 */
@ConfigurationProperties(prefix = "tian.api")
open class TianApiProperties(val token: String, var urls: List<TianApiUrl>)
data class TianApiUrl(var name: String, var code: String, var url: String)
enum class TianApiUrlCode(var value: String) {
    BULLETIN("bulletin"),
    ONE("one"),
    SAY_LOVE("saylove"),
    WEATHER("tianqi"),

}