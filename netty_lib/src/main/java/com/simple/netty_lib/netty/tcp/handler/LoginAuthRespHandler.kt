package com.simple.netty_lib.netty.tcp.handler

import android.util.Log
import com.simple.netty_lib.config.LOG_TAG
import com.simple.netty_lib.netty.tcp.NettyTCPIMS
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * @author: daiwenbo
 * @date: 2021/1/31-6:10 PM
 * @description: 握手认证消息响应处理handler 建立连接了
 */

class LoginAuthRespHandler(private val imsClient: NettyTCPIMS) : ChannelInboundHandlerAdapter() {


    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        Log.e(LOG_TAG, "握手认证消息响应：channelRead(LoginAuthRespHandler.kt:18)" + msg.toString())
        val status = -1
        //TODO
        // 握手成功，马上先发送一条心跳消息，至于心跳机制管理，交由HeartbeatHandler
        if (status==1) {
            val heartbeatMsg = imsClient.mIMSMsgReceivedListener?.getHeartbeatMsg()
            if (heartbeatMsg == null) return
            //TODO

            imsClient.sendMsg(heartbeatMsg)

            // 添加心跳消息管理handler
            imsClient.addHeartbeatHandler()
        }else{
            //握手失败且返回了消息一定是服务端认证没通过 所以这里需要关闭客户端
            imsClient.release()
        }
    }

}