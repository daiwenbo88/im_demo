package com.simple.netty_lib.config

class IMSOptions() {
     var implementationMode: ImplementationMode = ImplementationMode.Netty// 实现方式
     var communicationProtocol: CommunicationProtocol = CommunicationProtocol.TCP// 通信协议
     var transportProtocol: TransportProtocol = TransportProtocol.Json// 传输协议
     var connectTimeout= IMSConfig.CONNECT_TIMEOUT // 连接超时时间，单位：毫秒
     var reconnectInterval= IMSConfig.RECONNECT_INTERVAL // 重连间隔时间，单位：毫秒
     var reconnectCount= IMSConfig.RECONNECT_COUNT // 单个地址一个周期最大重连次数
     var foregroundHeartbeatInterval= IMSConfig.FOREGROUND_HEARTBEAT_INTERVAL // 应用在前台时心跳间隔时间，单位：毫秒
     var backgroundHeartbeatInterval= IMSConfig.BACKGROUND_HEARTBEAT_INTERVAL // 应用在后台时心跳间隔时间，单位：毫秒
     var autoResend = IMSConfig.AUTO_RESEND// 是否自动重发消息
     var resendInterval= IMSConfig.RESEND_INTERVAL // 自动重发间隔时间，单位：毫秒
     var resendCount = IMSConfig.RESEND_COUNT// 消息最大重发次数

     var serverList: List<String>? = null// 服务器地址列表

    constructor(builder: Builder?) : this() {
        if (builder == null) return
        implementationMode = builder.implementationMode
        communicationProtocol = builder.communicationProtocol
        transportProtocol = builder.transportProtocol
        connectTimeout = builder.connectTimeout
        reconnectInterval = builder.reconnectInterval
        reconnectCount = builder.reconnectCount
        foregroundHeartbeatInterval = builder.foregroundHeartbeatInterval
        backgroundHeartbeatInterval = builder.backgroundHeartbeatInterval
        autoResend = builder.autoResend
        resendInterval = builder.resendInterval
        resendCount = builder.resendCount
        serverList = builder.serverList
    }
    
    class Builder{
         var implementationMode: ImplementationMode = ImplementationMode.Netty// 实现方式
        
         var communicationProtocol: CommunicationProtocol = CommunicationProtocol.TCP// 通信协议
         var transportProtocol: TransportProtocol = TransportProtocol.Json// 传输协议
         var connectTimeout= IMSConfig.CONNECT_TIMEOUT // 连接超时时间，单位：毫秒
         var reconnectInterval= IMSConfig.RECONNECT_INTERVAL // 重连间隔时间，单位：毫秒
         var reconnectCount= IMSConfig.RECONNECT_COUNT // 单个地址一个周期最大重连次数
         var foregroundHeartbeatInterval= IMSConfig.FOREGROUND_HEARTBEAT_INTERVAL // 应用在前台时心跳间隔时间，单位：毫秒
         var backgroundHeartbeatInterval= IMSConfig.BACKGROUND_HEARTBEAT_INTERVAL // 应用在后台时心跳间隔时间，单位：毫秒
         var autoResend = IMSConfig.AUTO_RESEND// 是否自动重发消息
         var resendInterval= IMSConfig.RESEND_INTERVAL // 自动重发间隔时间，单位：毫秒
         var resendCount = IMSConfig.RESEND_COUNT// 消息最大重发次数
         var serverList: List<String>? = null// 服务器地址列表

        
        fun setImplementationMode(implementationMode: ImplementationMode): Builder {
            this.implementationMode = implementationMode
            return this
        }
        fun setCommunicationProtocol(communicationProtocol: CommunicationProtocol?): Builder {
            this.communicationProtocol = communicationProtocol?:CommunicationProtocol.TCP
            return this
        }
        fun setTransportProtocol(transportProtocol: TransportProtocol?): Builder {
            this.transportProtocol = transportProtocol?:TransportProtocol.Json
            return this
        }

        fun setConnectTimeout(connectTimeout: Int): Builder {
            this.connectTimeout = connectTimeout
            return this
        }

        fun setReconnectInterval(reconnectInterval: Int): Builder {
            this.reconnectInterval = reconnectInterval
            return this
        }

        fun setReconnectCount(reconnectCount: Int): Builder {
            this.reconnectCount = reconnectCount
            return this
        }

        fun setForegroundHeartbeatInterval(foregroundHeartbeatInterval: Int): Builder {
            this.foregroundHeartbeatInterval = foregroundHeartbeatInterval
            return this
        }

        fun setBackgroundHeartbeatInterval(backgroundHeartbeatInterval: Int): Builder {
            this.backgroundHeartbeatInterval = backgroundHeartbeatInterval
            return this
        }

        fun setAutoResend(autoResend: Boolean): Builder {
            this.autoResend = autoResend
            return this
        }

        fun setResendInterval(resendInterval: Int): Builder {
            this.resendInterval = resendInterval
            return this
        }

        fun setResendCount(resendCount: Int): Builder {
            this.resendCount = resendCount
            return this
        }

        fun setServerList(serverList: List<String>?): Builder {
            this.serverList = serverList
            return this
        }

        fun build(): IMSOptions? {
            return IMSOptions(this)
        }
    }
    
}