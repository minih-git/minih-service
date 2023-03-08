@file:Suppress("SpellCheckingInspection")

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
    var result: TianApiOneResult? = TianApiOneResult("", "", "", "", "", "")
}

data class TianApiOneResult(
    var oneid: String,
    var word: String,
    var wordfrom: String,
    var imgurl: String,
    var imgauthor: String,
    var date: String
)