package com.simple.netty_lib.interf

import android.content.Context
import com.simple.netty_lib.bean.IMSMsg
import com.simple.netty_lib.config.IMSOptions
import com.simple.netty_lib.listener.IMSConnectStatusListener
import com.simple.netty_lib.listener.IMSMsgReceivedListener
import com.simple.netty_lib.listener.IMSMsgSentStatusListener

/**
 * @author: daiwenbo
 * @date: 2021/1/30-3:23 PM
 * @description: IMS抽象接口
 */
interface IMSInterface {
    /**
     * @param context
     * @param options                   IMS初始化配置
     * @param imsConnectStatusListener  IMS连接状态监听
     * @param msgReceivedListener       IMS消息接收监听
     */
    fun init(
        context: Context?,
        options: IMSOptions?,
        imsConnectStatusListener: IMSConnectStatusListener?,
        msgReceivedListener: IMSMsgReceivedListener?
    ):Boolean

    /**
     * 链接
     */
    fun connect()

    /**
     * 重连
     * @param isFirstConnect Boolean 是否首次链接
     */
    fun reconnect(isFirstConnect: Boolean)

    /**
     * 发送消息
     */
    fun sendMsg(msg: IMSMsg)

    /**
     * 发送消息
     * @param msg IMSMsg
     * @param listener 监听发送状态
     */
    fun sendMsg(msg: IMSMsg, listener: IMSMsgSentStatusListener?)

    /**
     * 发送消息
     * @param msg IMSMsg
     * @param isJoinResendManager 是否加入消息重发管理器
     */
    fun sendMsg(msg: IMSMsg, isJoinResendManager: Boolean)

    /**
     * 发送消息
     * @param msg IMSMsg?
     * @param listener 监听发送状态
     * @param isJoinResendManager 是否加入消息重发管理器
     */
    fun sendMsg(msg: IMSMsg?, listener: IMSMsgSentStatusListener?, isJoinResendManager: Boolean)

    /**
     * 资源释放
     */
    fun release()
}