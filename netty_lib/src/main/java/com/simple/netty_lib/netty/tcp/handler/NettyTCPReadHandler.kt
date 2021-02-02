package com.simple.netty_lib.netty.tcp.handler

import android.util.Log
import com.simple.netty_lib.bean.IMSMsg
import com.simple.netty_lib.config.IMSConfig
import com.simple.netty_lib.config.IMSConnectStatus
import com.simple.netty_lib.config.LOG_TAG
import com.simple.netty_lib.netty.tcp.NettyTCPIMS
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * @author: daiwenbo
 * @date: 2021/1/30-5:27 PM
 * @description: TCP 消息监听
 */

class NettyTCPReadHandler(private val ims: NettyTCPIMS) : ChannelInboundHandlerAdapter() {
    companion object {
         val TAG: String = NettyTCPReadHandler::class.java.simpleName
    }

    /**
     * 在channel被启用的时候触发 (在建立连接的时候)
     * @param ctx ChannelHandlerContext
     */
    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
        Log.d(LOG_TAG, "TCP-消息监听:建立连接channelActive() ctx = $ctx")
    }

    /**
     * ims连接断开回调
     * @param ctx ChannelHandlerContext
     */
    override fun channelInactive(ctx: ChannelHandlerContext?) {
        Log.w(LOG_TAG, "TCP-消息监听 断开:channelInactive() ctx = $ctx")
        closeChannelAndReconnect(ctx)
    }

    /**
     * ims异常回调
     * @param ctx
     * @param cause
     */
    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        Log.e(LOG_TAG, "TCP-消息监听 IM异常:exceptionCaught() ctx = $ctx\tcause = $cause")
        closeChannelAndReconnect(ctx)
    }

    /**
     * 收到消息回调
     * @param ctx
     * @param msg
     * @throws Exception
     */
    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any) {
        Log.e(LOG_TAG,"TCP-消息监听:channelRead(NettyTCPReadHandler.kt:53)${msg as String}")
        ims.mIMSMsgReceivedListener?.onMsgReceived(msg as IMSMsg)
    }

    private fun closeChannelAndReconnect(ctx: ChannelHandlerContext?) {
        val channel = ctx?.channel()
        channel?.close()
        ctx?.close()
        // 回调连接状态
        ims.callbackIMSConnectStatus(IMSConnectStatus.ConnectFailed)
        // 触发重连
        Log.d(LOG_TAG,"closeChannelAndReconnect(NettyTCPReadHandler.kt:65)重新连接")
        ims.reconnect(false)
    }

}