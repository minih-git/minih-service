package com.minih.wx.controller

import jakarta.annotation.Resource
import me.chanjar.weixin.mp.api.WxMpMessageRouter
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*


/**
 * @author hubin
 * @date 2023/3/6
 * @desc
 */
@RestController
@RequestMapping("/api/callback")
class CallbackController {

    val log: Logger = LoggerFactory.getLogger(CallbackController::class.java)


    @Resource
    lateinit var wxMpService: WxMpService

    @Resource
    lateinit var router: WxMpMessageRouter


    @GetMapping("/gzh")
    fun getGzhCallBack(
        @RequestParam("signature") signature: String,
        @RequestParam("timestamp") timestamp: String,
        @RequestParam("nonce") nonce: String,
        @RequestParam("echostr") echostr: String,
    ): String {
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "false"
    }

    @PostMapping("/gzh", consumes = [MediaType.TEXT_XML_VALUE], produces = [MediaType.TEXT_XML_VALUE])
    fun postGzhCallBack(
        @RequestBody requestBody: String,
        @RequestParam("signature") signature: String,
        @RequestParam("timestamp") timestamp: String,
        @RequestParam("nonce") nonce: String,
        @RequestParam("openid") openid: String,
        @RequestParam(name = "encrypt_type", required = false) encType: String?,
        @RequestParam(name = "msg_signature", required = false) msgSignature: String?
    ): String {
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            throw IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        val inMessage: WxMpXmlMessage = WxMpXmlMessage.fromXml(requestBody)
        log.info("inMessage:{}", inMessage)
        val outMessage: WxMpXmlOutMessage = router.route(inMessage)
        log.info("outMessage:{}",outMessage.toXml())
        return outMessage.toXml()
    }
}

