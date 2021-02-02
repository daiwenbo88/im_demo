package com.simple.netty_lib.netty.tcp.handler

import android.util.Log
import com.simple.netty_lib.config.IMSConfig
import com.simple.netty_lib.config.LOG_TAG
import com.simple.netty_lib.netty.tcp.NettyTCPIMS
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * @author: daiwenbo
 * @date: 2021/1/31-3:51 PM
 * @description:收到服务器心跳消息响应处理handler
 */
class HeartbeatRespHandler(private val imsClient: NettyTCPIMS) : ChannelInboundHandlerAdapter(){

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        Log.e(LOG_TAG,"channelRead(HeartbeatRespHandler.kt:20)收到服务端心跳响应消息，message="+msg)
        val heartbeatMsg = imsClient.getHeartbeatMsg()
        if (null==heartbeatMsg)return
        if (true){
            //TODO
        }else{
            // 消息透传
            ctx.fireChannelRead(msg)
        }
    }
}