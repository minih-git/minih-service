package com.minih.wx.config

import com.minih.wx.component.MsgHandler
import jakarta.annotation.Resource
import me.chanjar.weixin.mp.api.WxMpMessageRouter
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
import me.chanjar.weixin.mp.config.WxMpConfigStorage
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.stream.Collectors

/**
 * @author hubin
 * @date 2023/3/6
 * @desc
 */
@Configuration
@EnableConfigurationProperties(WxMpProperties::class)
class WxMpConfiguration {

    @Resource
    lateinit var properties: WxMpProperties;

    @Resource
    lateinit var msgHandler: MsgHandler


    @Bean
    fun wxMpService(): WxMpService {
        val configs: List<WxMpPropertiesConfig> = this.properties.configs
        val service = WxMpServiceImpl();
        service.setMultiConfigStorages(
            configs
                .stream().map { (appId, secret, token, aesKey): WxMpPropertiesConfig ->
                    val configStorage = WxMpDefaultConfigImpl()
                    configStorage.appId = appId
                    configStorage.secret = secret
                    configStorage.token = token
                    configStorage.aesKey = aesKey
                    configStorage
                }.collect(
                    Collectors.toMap(
                        WxMpDefaultConfigImpl::getAppId,
                        { a -> a }
                    ) { o: WxMpConfigStorage?, _: WxMpConfigStorage? -> o })
        )
        return service
    }

    @Bean
    fun messageRouter(wxMpService: WxMpService): WxMpMessageRouter {
        val newRouter = WxMpMessageRouter(wxMpService);
        newRouter.rule().async(false).handler(this.msgHandler).end()
        return newRouter
    }


}