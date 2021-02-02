package com.simple.netty_lib.listener

import com.simple.netty_lib.bean.IMSMsg

/**
 * @author: daiwenbo
 * @date: 2021/1/30-3:32 PM
 * @description:IMS消息发送状态监听器
 */
interface IMSMsgSentStatusListener {
    /**
     * 消息发送成功
     */
    fun onSendSucceed(msg:IMSMsg)
    /**
     * 消息发送失败
     */
    fun onSendFailed(msg:IMSMsg,errMsg:String)
}