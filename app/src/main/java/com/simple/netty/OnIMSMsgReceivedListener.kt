package com.simple.netty

import com.simple.netty_lib.bean.IMSMsg
import com.simple.netty_lib.bean.OpCode
import com.simple.netty_lib.bean.Priority
import com.simple.netty_lib.listener.IMSMsgReceivedListener
import java.nio.charset.StandardCharsets

/**
 * @author: daiwenbo
 * @date: 2021/1/31-9:08 PM
 * @description: 消息分发
 */
class OnIMSMsgReceivedListener : IMSMsgReceivedListener {

    override fun onMsgReceived(msg: IMSMsg) {
    }

    override fun getHandshakeMsg(): IMSMsg? {
        val authMsg = IMSMsg()
        authMsg.id= System.currentTimeMillis() % 1000000000
        authMsg.op=OpCode.OP_AUTH //发送心跳包
        authMsg.priority=Priority.HIGH1
        val bytes=Token().toJson().toByteArray(StandardCharsets.UTF_8)
        authMsg.body=(bytes)
        authMsg.retryCount=3
        authMsg.needReceipt=true
        return authMsg
    }

    override fun getHeartbeatMsg(): IMSMsg? {
        return null
    }

}