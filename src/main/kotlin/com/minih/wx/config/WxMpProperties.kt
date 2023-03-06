package com.minih.wx.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author hubin
 * @date 2023/3/6
 * @desc
 */
@ConfigurationProperties(prefix = "wx.mp")
open class WxMpProperties(val configs: List<WxMpPropertiesConfig>){}
data class WxMpPropertiesConfig(val appId: String, val secret: String, val token: String, val aesKey: String)

