package com.simple.netty_lib.netty.tcp.handler

import android.util.Log
import com.simple.netty_lib.netty.tcp.NettyTCPIMS
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

/**
 * @author: daiwenbo
 * @date: 2021/1/31-6:46 PM
 * @description: 心跳任务管理器
 */
class HeartbeatHandler(private val imsClient: NettyTCPIMS) : ChannelInboundHandlerAdapter() {
    private var heartbeatTask: HeartbeatTask? = null

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any?) {
        super.userEventTriggered(ctx, evt)
        val stateEvent=evt as? IdleStateEvent
        stateEvent?.let {
            when(it.state()){
                // 规定时间内没收到服务端心跳包响应，进行重连操作
                IdleState.READER_IDLE -> imsClient.reconnect(false)
                // 规定时间内没向服务端发送心跳包，即发送一个心跳包
                IdleState.WRITER_IDLE -> {

                    if (heartbeatTask==null){
                        heartbeatTask=HeartbeatTask(ctx,imsClient)
                    }
                    Log.e("HeartbeatHandler","userEventTriggered(HeartbeatHandler.kt:30)心跳发送")
                    imsClient.mExecutors?.execWorkTask(heartbeatTask!!)
                }
                else ->{}
            }
        }
    }
    class HeartbeatTask(val ctx: ChannelHandlerContext,val imsClient: NettyTCPIMS):Runnable{
        override fun run() {
            val heartbeatMsg=imsClient.getHeartbeatMsg()
            if (ctx.channel().isActive&&null!=heartbeatMsg){
                imsClient.sendMsg(heartbeatMsg,false)
            }
        }
    }
}