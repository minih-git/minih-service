package com.minih.wx.component

import cn.hutool.core.bean.BeanUtil
import cn.hutool.http.ContentType
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpUtil
import com.minih.wx.config.TianApiProperties
import com.minih.wx.config.TianApiUrl
import com.minih.wx.dto.TianApiParams
import com.minih.wx.dto.TianApiResponse
import com.minih.wx.dto.TianApiResponseResult
import jakarta.annotation.Resource
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.stream.Collector
import java.util.stream.Collectors

/**
 * @author hubin
 * @date 2023/3/8
 * @desc
 */
@Component
@EnableConfigurationProperties(TianApiProperties::class)
class TianApiRequestHandler(private val properties: TianApiProperties) {

    fun getData(apiCode: String, params: TianApiParams, clazz: Class<TianApiResponse>): TianApiResponse {
        if (apiCode.isBlank()) {
            return TianApiResponse("000", "apiCode不能为空", null);
        }
        if (properties.urls.stream().map(TianApiUrl::code).toList().none { it == apiCode }) {
            return TianApiResponse("000", "未找到对应的api", null);
        }
        val resStr = HttpRequest.post(properties.urls.find { it.code == apiCode }?.url)
            .contentType(ContentType.FORM_URLENCODED.value)
            .form("key", properties.token)
            .form(BeanUtil.beanToMap(params))
            .execute()
        return BeanUtil.copyProperties(resStr, clazz)

    }


}