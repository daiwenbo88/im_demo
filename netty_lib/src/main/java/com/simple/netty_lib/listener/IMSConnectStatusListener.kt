package com.simple.netty_lib.listener

/**
 * @author: daiwenbo
 * @date: 2021/1/30-3:26 PM
 * @description: IMS连接状态监听器
 */
interface IMSConnectStatusListener {
    /**
     * 断开链接
     */
    fun onUnconnected()

    /**
     * 连接中
     */
    fun onConnecting()

    /**
     * 链接完成
     */
    fun onConnected()

    /**
     * 链接失败
     */
    fun onConnectFailed(errCode:Int,errMsg:String)

}