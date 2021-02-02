package com.simple.netty_lib.config

/**
 * @author: daiwenbo
 * @date: 2021/1/30-3:11 PM
 * @description: IM 配置
 */
const val LOG_TAG: String = "im_simple"

class IMSConfig {

    companion object {

        //链接超时时间 单位：毫秒
        const val CONNECT_TIMEOUT = 10 * 1000

        //重连间隔时间 单位：毫秒
        const val RECONNECT_INTERVAL = 10 * 1000

        //单个地址一个周期最大重连次数
        const val RECONNECT_COUNT = 3

        //应用在前台时心跳间隔时间，单位：毫秒
        const val FOREGROUND_HEARTBEAT_INTERVAL = 8 * 1000

        //应用在后台时心跳间隔时间，单位：毫秒
        const val BACKGROUND_HEARTBEAT_INTERVAL = 30 * 1000

        // 是否自动重发消息
        const val AUTO_RESEND = true

        //自动重发间隔时间，单位：毫秒
        const val RESEND_INTERVAL = 3 * 1000

        // 消息最大重发次数
        const val RESEND_COUNT = 5

    }
}