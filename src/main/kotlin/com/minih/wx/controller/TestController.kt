package com.minih.wx.controller

import com.minih.wx.component.TianApiRequestHandler
import com.minih.wx.config.TianApiUrlCode
import com.minih.wx.dto.TianApiOneParams
import com.minih.wx.dto.TianApiOneResponse
import com.minih.wx.dto.TianApiParams
import com.minih.wx.dto.TianApiResponse
import jakarta.annotation.Resource
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * @author hubin
 * @date 2023/3/7
 * @desc
 */
@RestController
@RequestMapping("/api/test")
class TestController(private val wxMpService: WxMpService, private val tainApiService: TianApiRequestHandler) {

    val log: Logger = LoggerFactory.getLogger(TestController::class.java)

    @GetMapping("/senMsg")
    fun test(msg: String?) {
        val wxUserList = this.wxMpService.userService.userList("")
        val data = tainApiService.getData(TianApiUrlCode.ONE.value, TianApiOneParams(0, ""), TianApiOneResponse::class);
        if (data is TianApiOneResponse) {
            wxMpService.kefuService.sendKefuMessage(
                WxMpKefuMessage
                    .TEXT()
                    .toUser(wxUserList.openids[0])
                    .content(data.result?.word)
                    .build()
            )
            return;
        }
        if (data is TianApiResponse) {

            log.warn(data.msg)
            return;
        }


    }

}