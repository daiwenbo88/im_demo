package com.simple.netty_lib

import android.content.Context
import android.util.Log
import com.simple.netty_lib.config.IMSConfig
import com.simple.netty_lib.config.IMSOptions
import com.simple.netty_lib.config.LOG_TAG
import com.simple.netty_lib.interf.IMSInterface
import com.simple.netty_lib.listener.IMSConnectStatusListener
import com.simple.netty_lib.listener.IMSMsgReceivedListener
import com.simple.netty_lib.netty.tcp.NettyTCPIMS

/**
 * @author: daiwenbo
 * @date: 2021/1/31-3:22 PM
 * @description:
 */
class IMSKit {
    var ims: IMSInterface?=null

    companion object {
        val TAG: String = IMSKit::class.java.simpleName
        fun getInstance() = Helper.instance
    }

    private object Helper {
        val instance = IMSKit()
    }

    fun init(
        context: Context?,
        options: IMSOptions?,
        connectStatusListener: IMSConnectStatusListener?,
        msgReceivedListener: IMSMsgReceivedListener?
    ): Boolean {
        Log.d(LOG_TAG, "IMSKit初始化开始")
        if (context == null) {
            Log.w(LOG_TAG, "IMSKit初始化失败：Context 为 null")
            return false
        }

        if (options == null) {
            Log.w(LOG_TAG, "IMSKit初始化失败：IMSOptions 为 null")
            return false
        }

        var initialized=false

        ims=IMSFactory.getIMS(options.implementationMode, options.communicationProtocol).apply {
            initialized=init(context, options, connectStatusListener, msgReceivedListener)
        }

        if (!initialized){
            Log.w(LOG_TAG, "IMSKit初始化失败")
        }
        return initialized
    }

    fun connect(){
        if (ims==null){
            Log.d(LOG_TAG, "IMSKit启动失败")
            return
        }
        ims?.connect()
    }

    fun disconnect(){
        if (ims==null){
            return
        }
        ims?.release()
    }


}