package com.minih.wx.dto

/**
 * @author hubin
 * @date 2023/3/8
 * @desc
 */
interface TianApiParams

class TianApiResponse<T> {
    var code: String = ""
    var msg: String = ""
    var result: T? = null
}

data class TianApiOneParams(var rand: Int?, var date: String?) : TianApiParams

data class TianApiOneResponse(
    var oneid: String,
    var word: String,
    var wordfrom: String,
    var imgurl: String,
    var imgauthor: String,
    var date: String
):TianApiResponse()