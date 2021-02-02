package com.simple.netty_lib.bean

/**
 * @author: daiwenbo
 * @date: 2021/1/30-3:30 PM
 * @description: IMS消息
 */
class IMSMsg {
     var id: Long = 0
     var uniqueId: String = ""
     var uid: Long = 0
     var roomId: String = ""
     var version: Int = 5
     var op = 0
     var needRead = false
     var read = false
     var needReceipt = true
     var receipt = false
     var done = false
     var priority: Int = Priority.NORMAL
     var actionTime: Long = 0
     var genTime: Long = 0
     var timeout = -1
     var retryCount = -1
     var actionCount = 0
     var body = ByteArray(0)
}