package com.minih.wx.component

import com.minih.wx.service.ChatGPTService
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpMessageHandler
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


/**
 * @author hubin
 * @date 2023/3/6
 * @desc
 */
@Component
class MsgHandler(val chatGPTService: ChatGPTService) : WxMpMessageHandler {
    val log: Logger = LoggerFactory.getLogger(MsgHandler::class.java)
    override fun handle(
        wxMessage: WxMpXmlMessage?,
        context: MutableMap<String, Any>?,
        wxMpService: WxMpService?,
        sessionManager: WxSessionManager?
    ): WxMpXmlOutMessage {
        val returnMsg = chatGPTService.textChat(wxMessage?.fromUser, wxMessage?.content);
        wxMpService?.kefuService?.sendKefuMessage(
            WxMpKefuMessage
                .TEXT()
                .toUser(wxMessage?.fromUser)
                .content(returnMsg?.content?.trim())
                .build()
        )
        return WxMpXmlOutTextMessage.TEXT().content(returnMsg?.content?.trim())
            .fromUser(wxMessage?.toUser).toUser(wxMessage?.fromUser).build()
    }

}