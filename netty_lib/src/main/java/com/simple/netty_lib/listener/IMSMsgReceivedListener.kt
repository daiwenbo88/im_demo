package com.simple.netty_lib.listener

import com.simple.netty_lib.bean.IMSMsg

/**
 * @author: daiwenbo
 * @date: 2021/1/30-3:29 PM
 * @description: IMS消息接收监听器
 */
interface IMSMsgReceivedListener {
    /**
     * 消息分发
     * @param msg IMSMsg
     */
    fun onMsgReceived(msg: IMSMsg)
    /**
     * 获取握手响应
     * @return String
     */
    fun getHandshakeMsg():IMSMsg?
    /**
     * 获取心跳消息
     * @return String
     */
    fun getHeartbeatMsg():IMSMsg?

}