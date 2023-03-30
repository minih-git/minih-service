package com.minih.wx.component

import cn.hutool.core.bean.BeanUtil
import cn.hutool.http.ContentType
import cn.hutool.http.HttpRequest
import cn.hutool.json.JSONUtil
import com.minih.wx.config.TianApiProperties
import com.minih.wx.config.TianApiUrl
import com.minih.wx.dto.TianApiParams
import com.minih.wx.dto.TianApiResponse
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

/**
 * @author hubin
 * @date 2023/3/8
 * @desc
 */
@Component
@EnableConfigurationProperties(TianApiProperties::class)
class TianApiRequestHandler(private val properties: TianApiProperties) {

    fun getData(apiCode: String, params: TianApiParams, clazz: KClass<*>): Any {
        if (apiCode.isBlank()) {
            return TianApiResponse("000", "apiCode不能为空");
        }
        if (properties.urls.stream().map(TianApiUrl::code).toList().none { it == apiCode }) {
            return TianApiResponse("000", "未找到对应的api");
        }
        if (!clazz.isSubclassOf(TianApiResponse::class)) {
            return TianApiResponse("000", "返回数据类型不正确");
        }
        val url = properties.baseUrl + properties.urls.find { it.code == apiCode }?.url;
        val resStr = HttpRequest.post(url)
            .contentType(ContentType.FORM_URLENCODED.value)
            .form("key", properties.token)
            .form(BeanUtil.beanToMap(params))
            .execute().body()
        return JSONUtil.toBean(resStr, clazz.java)

    }


}