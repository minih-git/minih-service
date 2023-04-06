package com.minih.wx.controller

import com.minih.wx.component.SseEmitterMap
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

/**
 * @author hubin
 * @date 2023/4/6
 * @desc
 */
@RestController
@CrossOrigin
@RequestMapping("/sse")
class SseEmitterController {

    @RequestMapping("/connect")
    fun connect(uuid: String): SseEmitter {
        if (uuid.isEmpty()) {
            throw RuntimeException()
        }
        var emitter = SseEmitterMap[uuid]
        if (emitter == null) {
            emitter = SseEmitter(0L)
            emitter.onCompletion {
                SseEmitterMap.remove(uuid)
            }
            emitter.onError {
                SseEmitterMap.remove(uuid)
            }
            SseEmitterMap[uuid] = emitter
        }
        emitter.send("连接成功")
        return emitter;
    }

    @RequestMapping("/close")
    fun close(uuid: String) {
        if (uuid.isEmpty()) {
            throw RuntimeException()
        }
        val emitter = SseEmitterMap[uuid] ?: return
        SseEmitterMap.remove(uuid)
        emitter.complete()
    }


}