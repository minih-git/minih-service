package com.minih.wx.component

import com.aallam.openai.api.BetaOpenAI
import com.minih.wx.service.ChatGPTService
import kotlinx.coroutines.DelicateCoroutinesApi
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @author hubin
 * @date 2023/3/6
 * @desc
 */
@Component
class MsgHandler(val chatGPTService: ChatGPTService) : WxMpMessageHandler {
    val log: Logger = LoggerFactory.getLogger(MsgHandler::class.java)

    @OptIn(BetaOpenAI::class, DelicateCoroutinesApi::class)
    override fun handle(
        wxMessage: WxMpXmlMessage?,
        context: MutableMap<String, Any>?,
        wxMpService: WxMpService?,
        sessionManager: WxSessionManager?
    ): WxMpXmlOutMessage? {
        GlobalScope.launch {
            val returnMsg = chatGPTService.textChat(wxMessage?.fromUser, wxMessage?.content);
            wxMpService?.kefuService?.sendKefuMessage(
                WxMpKefuMessage
                    .TEXT()
                    .toUser(wxMessage?.fromUser)
                    .content(returnMsg?.content?.trim())
                    .build()
            )
        }
        return null
    }

}