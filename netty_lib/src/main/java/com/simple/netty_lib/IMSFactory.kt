package com.simple.netty_lib

import com.simple.netty_lib.config.CommunicationProtocol
import com.simple.netty_lib.config.ImplementationMode
import com.simple.netty_lib.interf.IMSInterface
import com.simple.netty_lib.netty.tcp.NettyTCPIMS

/**
 * @author: daiwenbo
 * @date: 2021/1/31-3:28 PM
 * @description:IM工厂方法
 */
class IMSFactory {
    companion object {
        fun getIMS(implementationMode: ImplementationMode, communicationProtocol: CommunicationProtocol): IMSInterface {
            return NettyTCPIMS.getInstance()
        }
    }
}
