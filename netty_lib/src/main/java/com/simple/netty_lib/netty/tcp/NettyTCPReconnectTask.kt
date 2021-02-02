package com.simple.netty_lib.netty.tcp

import android.util.Log
import com.simple.netty_lib.config.IMSConfig
import com.simple.netty_lib.config.IMSConnectStatus
import com.simple.netty_lib.config.LOG_TAG
import io.netty.channel.Channel

/**
 * @author: daiwenbo
 * @date: 2021/1/31-2:36 PM
 * @description: 链接服务器
 */
class NettyTCPReconnectTask(private val ims: NettyTCPIMS) : Runnable {

    override fun run() {
        try {
            // 重连时，释放工作线程组，也就是停止心跳
            ims.mExecutors?.destroyWorkLoopGroup()
            // ims未关闭并且网络可用的情况下，才去连接
            while (!ims.isClosed.get() && ims.isNetworkAvailable) {
                val status: IMSConnectStatus = content()
                if (status === IMSConnectStatus.Connected) {
                    ims.callbackIMSConnectStatus(status)
                    break // 连接成功，跳出循环
                }
                if (status === IMSConnectStatus.ConnectFailed
                    || status === IMSConnectStatus.ConnectFailed_IMSClosed
                    || status === IMSConnectStatus.ConnectFailed_ServerListEmpty
                    || status === IMSConnectStatus.ConnectFailed_ServerEmpty
                    || status === IMSConnectStatus.ConnectFailed_ServerIllegitimate
                    || status === IMSConnectStatus.ConnectFailed_NetworkUnavailable
                ) {
                    ims.callbackIMSConnectStatus(status)
                    if (ims.isClosed.get() || !ims.isNetworkAvailable) return
                    try {
                        val time = ims.mIMSOptions?.reconnectInterval?.toLong() ?: 0L
                        Thread.sleep(time)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
            // 标识重连任务停止
            ims.isReconnecting.compareAndSet(true, false)
        }
    }

    /**
     * 链接服务器
     * @return IMSConnectStatus
     */
    private fun content(): IMSConnectStatus {
        if (ims.isClosed.get()) return IMSConnectStatus.ConnectFailed_IMSClosed
        if (!ims.isNetworkAvailable) return IMSConnectStatus.ConnectFailed_NetworkUnavailable
        val iMSOptions = ims.mIMSOptions
        iMSOptions?.let {
            val serverList = it.serverList
            if (serverList.isNullOrEmpty()) return IMSConnectStatus.ConnectFailed_ServerListEmpty
            ims.initBootStrap()
            for ((index, value) in serverList.withIndex()) {
                //获取host
                val params = value.split(" ")
                if (params.isNullOrEmpty() || params.size < 2) return IMSConnectStatus.ConnectFailed_ServerIllegitimate
                if (0 == index) {//设置状态为连接中
                    ims.callbackIMSConnectStatus(IMSConnectStatus.Connecting)
                }
                for (i in 0..it.reconnectCount) {
                    Log.d(LOG_TAG, "正在进行【${value}】的第${i}次连接")
                    try {
                        if (ims.isClosed.get()) return IMSConnectStatus.ConnectFailed_IMSClosed
                        if (!ims.isNetworkAvailable) return IMSConnectStatus.ConnectFailed_NetworkUnavailable
                        val channel: Channel? = toServer(params[0], params[1].toInt())
                        if (channel != null && channel.isOpen && channel.isActive && channel.isRegistered && channel.isWritable) {
                            ims.channel=channel
                            Log.d(LOG_TAG,"content(NettyTCPReconnectTask.kt:84)链接成功")
                            return IMSConnectStatus.Connected//连接已成功
                        } else {
                            if (i == it.reconnectCount) {
                                // 如果当前已达到最大重连次数，并且是最后一个服务器地址，则回调连接失败
                                if (index == serverList.size - 1) {
                                    Log.w(LOG_TAG, "【${value}】连接失败")
                                    return IMSConnectStatus.ConnectFailed
                                } else {
                                    // 一个服务器地址连接失败后，延时指定时间再去进行下一个服务器地址的连接
                                    Log.w(
                                        LOG_TAG, "【${value}】连接失败，正在等待进行下一个服务器地址的重连，当前重连延时时长：${it.reconnectInterval}ms",
                                    )
                                    Thread.sleep(it.reconnectInterval.toLong())
                                }
                            } else {
                                // 连接失败，则线程休眠（重连间隔时长 / 2 * n） ms
                                val delayTime: Int =
                                    it.reconnectInterval + it.reconnectInterval / 2 * i
                                Log.w(LOG_TAG, "【%1\$s】连接失败，正在等待重连，当前重连延时时长：${value}:${delayTime}ms")
                                Thread.sleep(delayTime.toLong())
                            }
                        }
                    } catch (e: InterruptedException) {
                        break// 线程被中断，则强制关闭
                    }
                }
            }
        }
        //链接失败
        return IMSConnectStatus.ConnectFailed
    }

    /**
     * 获取 channel
     * @param host String
     * @param port Int
     * @return Channel?
     */

    private fun toServer(host: String, port: Int): Channel? {
        var channel: Channel? = null
        try {
            ims.bootstrap?.let {
                channel = it.connect(host, port).sync().channel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return channel
    }

}