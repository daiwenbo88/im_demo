package com.simple.netty

import com.simple.netty_lib.listener.IMSConnectStatusListener

/**
 * @author: daiwenbo
 * @date: 2021/1/31-9:06 PM
 * @description: 链接状态监听
 */
class OnIMSConnectStatusListener :IMSConnectStatusListener{
    override fun onUnconnected() {
    }

    override fun onConnecting() {
    }

    override fun onConnected() {
    }

    override fun onConnectFailed(errCode: Int, errMsg: String) {
    }

}