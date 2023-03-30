@file:Suppress("SpellCheckingInspection", "PropertyName")

package com.minih.wx.dto

/**
 * @author hubin
 * @date 2023/3/8
 * @desc
 */
interface TianApiParams

open class TianApiResponse(var code: String, var msg: String)

data class TianApiOneParams(var rand: Int?, var date: String?) : TianApiParams

class TianApiOneResponse : TianApiResponse("", "") {
    var result: TianApiOneResult? = TianApiOneResult()

    class TianApiOneResult {
        var oneid: String = ""
        var word: String = ""
        var wordfrom: String = ""
        var imgurl: String = ""
        var imgauthor: String = ""
        var date: String = ""
    }
}


data class TianApiWeatherParams(var city: String, var type: String) : TianApiParams

class TianApiWeatherResponse : TianApiResponse("", "") {
    var result: TianApiWeatherResult? = TianApiWeatherResult()

    class TianApiWeatherResult {
        var date: String = "" //日期
        var week: String = "" //星期
        var province: String = ""//省份
        var area: String = ""//结果地区（市/区/县）
        var areaid: String = ""//城市天气ID
        var weather: String = ""//	天气状况
        var weathercode: String = ""//天气代码
        var real: String = ""//实时气温（七天为空）
        var lowest: String = ""//最低温（夜间温度）
        var highest: String = ""//最高温（日间温度）
        var wind: String = ""//风向（方位）
        var windspeed: String = ""//风速（km/h）
        var windsc: String = ""//风力
        var sunrise: String = ""//日出时间
        var sunset: String = ""//日落时间
        var moonrise: String = ""//月升时间
        var moondown: String = ""//月落时间
        var pcpn: String = ""//降雨量（毫米）
        var uv_index: String = ""//紫外线强度指数
        var aqi: String = ""//空气质量指数（七天无此字段）
        var quality: String = ""//空气质量提示（七天无此字段）
        var vis: String = ""//能见度（公里）
        var humidity: String = ""//相对湿度（百分比）
        var alarmlist: List<TianApiWeatherAlarm> = ArrayList()//天气预警列表（七天无此字段）
        var tips: String = ""//生活指数提示

        class TianApiWeatherAlarm {
            var province: String = ""//预警省份
            var city: String = ""//预警城市
            var level: String = ""//预警级别
            var type: String = ""//预警类型
            var content: String = ""//预警内容
            var time: String = ""//预警时间
        }

    }
}