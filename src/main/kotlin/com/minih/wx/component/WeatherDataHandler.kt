package com.minih.wx.component

import com.minih.wx.config.TianApiUrlCode
import com.minih.wx.controller.TestController
import com.minih.wx.dto.*
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * @author hubin
 * @date 2023/3/8
 * @desc
 */
@Component
class WeatherDataHandler(private val tainApiService: TianApiRequestHandler) : IPushDataHandler {
    val log: Logger = LoggerFactory.getLogger(WeatherDataHandler::class.java)

    override fun buildData(): String {
        val data = tainApiService.getData(
            TianApiUrlCode.ONE.value,
            TianApiWeatherParams("上海", "1"),
            TianApiWeatherResponse::class
        );
        if (data is TianApiWeatherResponse) {
            var weatherTemplate = """
                ${data.result?.area}
                |${data.result?.weather},
                |气温:${data.result?.lowest} - ${data.result?.highest}
                |当前温度:${data.result?.real}
                |风向:${data.result?.wind} 风速:${data.result?.windspeed} 风力:${data.result?.windsc}
                |空气质量指数:${data.result?.aqi} 空气质量:${data.result?.quality} 
                |相对湿度:${data.result?.humidity}
                |生活小贴士:${data.result?.tips}
            """.trimIndent()
            if (data.result?.alarmlist?.size!! > 0) {
                weatherTemplate = """
                    $weatherTemplate
                    |天气预警:
                """.trimIndent()
                var alarm = "";
                var i = 1;
                data.result?.alarmlist?.forEach { item ->
                    alarm = """
                        $alarm
                        |$i、${item.content}
                    """.trimIndent()
                    i++;
                }
                weatherTemplate = """
                    $weatherTemplate
                    |$alarm
                """.trimIndent()
            }
            return weatherTemplate;
        }
        if (data is TianApiResponse) {
            log.warn("获取天气信息出现问题,{}", data.msg)
        }
        return "";
    }
}