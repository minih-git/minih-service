package com.minih.wx.compoent

import cn.hutool.core.collection.CollUtil
import cn.hutool.http.Header
import cn.hutool.http.HttpRequest
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import com.minih.wx.controller.CallbackController
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpMessageHandler
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.stream.Collectors

/**
 * @author hubin
 * @date 2023/3/6
 * @desc
 */
@Component
class MsgHandler : WxMpMessageHandler {
    val log: Logger = LoggerFactory.getLogger(MsgHandler::class.java)
    override fun handle(
        wxMessage: WxMpXmlMessage?,
        context: MutableMap<String, Any>?,
        wxMpService: WxMpService?,
        sessionManager: WxSessionManager?
    ): WxMpXmlOutMessage {

        val realPrompt = "\n请回答以下问题:\n${wxMessage?.content}\n {}"
        val jsonObject = JSONObject()
        jsonObject.putOnce("model", "text-davinci-003")
        jsonObject.putOnce("prompt", realPrompt)
        jsonObject.putOnce("max_tokens", 2048)
        jsonObject.putOnce("temperature", 0)
        jsonObject.putOnce("top_p", 1)
        jsonObject.putOnce("frequency_penalty", 0)
        jsonObject.putOnce("presence_penalty", 0.6)
        jsonObject.putOnce("stop", arrayOf("{}"))
        val result = HttpRequest.post(" https://api.openai.com/v1/completions")
            .header(Header.AUTHORIZATION, "Bearer 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")
            .body(jsonObject.toString())
            .execute().body()
        val resultObj = JSONUtil.parseObj(result)
        val returnMsg = CollUtil.getFirst(resultObj.getJSONArray("choices").stream().map { obj: Any? ->
            JSONUtil.parseObj(
                obj
            )
        }.collect(Collectors.toList())).getStr("text")
        log.info("returnMsg:{}", returnMsg)
        return WxMpXmlOutTextMessage.TEXT().content(returnMsg)
            .fromUser(wxMessage?.toUser).toUser(wxMessage?.fromUser).build()
    }

}
