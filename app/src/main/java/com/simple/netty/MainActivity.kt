package com.simple.netty

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simple.netty_lib.IMSKit
import com.simple.netty_lib.config.CommunicationProtocol
import com.simple.netty_lib.config.IMSOptions
import com.simple.netty_lib.config.ImplementationMode
import com.simple.netty_lib.config.TransportProtocol
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serverList: MutableList<String> = ArrayList()
        serverList.add("10.10.10.136 3101")

        val options: IMSOptions? = IMSOptions.Builder()
            .setImplementationMode(ImplementationMode.Netty)
            .setCommunicationProtocol(CommunicationProtocol.TCP)
            .setTransportProtocol(TransportProtocol.Json)
            .setServerList(serverList)
            .build()
        IMSKit.getInstance().init(applicationContext,options,OnIMSConnectStatusListener(),OnIMSMsgReceivedListener())
        IMSKit.getInstance().connect()
    }
}