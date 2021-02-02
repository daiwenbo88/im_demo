package com.simple.netty_lib.netty.tcp

import com.simple.netty_lib.netty.tcp.handler.HeartbeatRespHandler
import com.simple.netty_lib.netty.tcp.handler.LoginAuthRespHandler
import com.simple.netty_lib.netty.tcp.handler.NettyTCPReadHandler
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.bytes.ByteArrayEncoder
import io.netty.handler.codec.string.StringDecoder
import io.netty.util.CharsetUtil

/**
 * @author: daiwenbo
 * @date: 2021/1/30-5:31 PM
 * @description: Channel初始化配置
 */
class NettyTCPChannelInitializerHandler(private val ims: NettyTCPIMS) :
    ChannelInitializer<Channel>() {
    override fun initChannel(channel: Channel?) {
        channel?.pipeline()?.apply {
            // netty提供的自定义长度解码器，解决TCP拆包/粘包问题
           /* addLast("frameEncoder", LengthFieldPrepender(2))
            addLast(
                "frameDecoder", LengthFieldBasedFrameDecoder(
                    65535,
                    0, 2, 0, 2
                )
            )*/
            // 增加编解码支持
          /*  addLast("decoder",  StringDecoder(CharsetUtil.UTF_8))
            addLast("encoder",  StringDecoder(CharsetUtil.UTF_8))*/
            // 增加protobuf编解码支持
            //addLast(ProtobufEncoder())
            //addLast(ProtobufDecoder(MessageProtobuf.Msg.getDefaultInstance()))
            // 握手认证消息响应处理handler
             addLast("frameEncoder", LengthFieldPrepender(4));
             addLast("bytesEncoder", ByteArrayEncoder())
            addLast(
                LoginAuthRespHandler::class.java.simpleName,
                LoginAuthRespHandler(ims)
            )
            // 心跳消息响应处理handler
            addLast(
                HeartbeatRespHandler::class.java.simpleName,
                HeartbeatRespHandler(ims)
            )
            // 接收消息处理handler
            addLast(
                NettyTCPReadHandler::class.java.simpleName,
                NettyTCPReadHandler(ims)
            )
        }
    }
}