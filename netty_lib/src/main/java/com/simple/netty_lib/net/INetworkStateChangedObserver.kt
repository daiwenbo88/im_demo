package com.simple.netty_lib.net

/**
 * @author: daiwenbo
 * @date: 2021/1/30-4:47 PM
 * @description: 网络状态监听回调
 */
interface INetworkStateChangedObserver {
    //网络活跃的
    fun onNetworkAvailable()
    //网络断开
    fun onNetworkUnavailable()
}