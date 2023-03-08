package com.minih.wx.controller

import com.minih.wx.component.TianApiRequestHandler
import com.minih.wx.config.TianApiUrlCode
import com.minih.wx.dto.TianApiOneParams
import com.minih.wx.dto.TianApiOneResponse
import com.minih.wx.dto.TianApiParams
import jakarta.annotation.Resource
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage
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

    @GetMapping("/senMsg")
    fun test(msg: String?) {
        val wxUserList = this.wxMpService.userService.userList("")

        tainApiService.getData(TianApiUrlCode.ONE.value, TianApiOneParams(0, ""), TianApiOneResponse)


        wxMpService.kefuService.sendKefuMessage(
            WxMpKefuMessage
                .TEXT()
                .toUser(wxUserList.openids[0])
                .content(msg)
                .build()
        )
    }

}