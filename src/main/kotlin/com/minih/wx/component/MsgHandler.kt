package com.minih.wx.component

import cn.hutool.json.JSONUtil
import com.plexpt.chatgpt.ChatGPT
import com.plexpt.chatgpt.entity.chat.ChatCompletion
import com.plexpt.chatgpt.entity.chat.Message
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpMessageHandler
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component


/**
 * @author hubin
 * @date 2023/3/6
 * @desc
 */
@Component
class MsgHandler(val redisTemplate: RedisTemplate<String, String>) : WxMpMessageHandler {
    val log: Logger = LoggerFactory.getLogger(MsgHandler::class.java)
    override fun handle(
        wxMessage: WxMpXmlMessage?,
        context: MutableMap<String, Any>?,
        wxMpService: WxMpService?,
        sessionManager: WxSessionManager?
    ): WxMpXmlOutMessage {



        wxMpService?.kefuService?.sendKefuMessage(
            WxMpKefuMessage
                .TEXT()
                .toUser(wxMessage?.fromUser)
                .content(res.content.trim())
                .build()
        )
        return WxMpXmlOutTextMessage.TEXT().content(res.content.trim())
            .fromUser(wxMessage?.toUser).toUser(wxMessage?.fromUser).build()
    }

}