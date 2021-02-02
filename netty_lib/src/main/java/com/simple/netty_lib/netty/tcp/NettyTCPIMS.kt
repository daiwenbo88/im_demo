package com.simple.netty_lib.netty.tcp

import android.content.Context
import android.util.Log
import com.simple.netty_lib.bean.IMSMsg
import com.simple.netty_lib.config.IMSConnectStatus
import com.simple.netty_lib.config.IMSOptions
import com.simple.netty_lib.config.LOG_TAG
import com.simple.netty_lib.interf.IMSInterface
import com.simple.netty_lib.listener.IMSConnectStatusListener
import com.simple.netty_lib.listener.IMSMsgReceivedListener
import com.simple.netty_lib.listener.IMSMsgSentStatusListener
import com.simple.netty_lib.net.INetworkStateChangedObserver
import com.simple.netty_lib.net.NetworkManager
import com.simple.netty_lib.netty.tcp.handler.HeartbeatHandler
import com.simple.netty_lib.netty.tcp.handler.NettyTCPReadHandler
import com.simple.netty_lib.utils.ExecutorServiceFactory
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.ChannelPipeline
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.IdleStateHandler
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author: daiwenbo
 * @date: 2021/1/30-3:54 PM
 * @description: 基于Netty实现的TCP协议客户端
 */
class NettyTCPIMS : IMSInterface, INetworkStateChangedObserver {
    // 是否已初始化成功
    private var initialized: Boolean = false
    private var mContext: Context? = null

    // ims配置项
    var mIMSOptions: IMSOptions? = null

    // ims连接状态监听器
    private var mIMSConnectStatusListener: IMSConnectStatusListener? = null

    // ims消息接收监听器
    var mIMSMsgReceivedListener: IMSMsgReceivedListener? = null

    // 线程池组
    var mExecutors: ExecutorServiceFactory? = null

    // ims是否已关闭
    var isClosed: AtomicBoolean = AtomicBoolean(false)

    // 是否正在进行重连
    var isReconnecting: AtomicBoolean = AtomicBoolean(false)

    //网络是否可用标识
    var isNetworkAvailable: Boolean = true

    @Volatile //ims连接状态
    private var mIMSConnectStatus: IMSConnectStatus? = null


     var bootstrap: Bootstrap? = null
     var channel: Channel? = null

    companion object {
        val TAG: String = NettyTCPIMS::class.java.simpleName
        fun getInstance() = Helper.instance
    }

    private object Helper {
        val instance = NettyTCPIMS()
    }

    override fun init(
        context: Context?,
        options: IMSOptions?,
        connectStatusListener: IMSConnectStatusListener?,
        msgReceivedListener: IMSMsgReceivedListener?
    ): Boolean {

        if (null == context) {
            Log.d(LOG_TAG, "初始化失败：Context is null.")
            initialized = false
            return false
        }
        if (null == options) {
            Log.d(LOG_TAG, "初始化失败：IMSOptions is null.")
            initialized = false
            return false
        }
        mContext = context
        mIMSOptions = options
        mIMSConnectStatusListener = connectStatusListener
        mIMSMsgReceivedListener = msgReceivedListener

        mExecutors = ExecutorServiceFactory().apply {
            // 初始化重连线程池
            initBossLoopGroup()
        }
        NetworkManager.getInstance().registerObserver(context, this)
        //标识ims初始化成功
        initialized = true
        // 标识ims已打开
        isClosed = AtomicBoolean(false)
        callbackIMSConnectStatus(IMSConnectStatus.Unconnected)
        return initialized
    }

    override fun connect() {
        if (!initialized) {
            Log.w(LOG_TAG, "IMS初始化失败，initialized is false")
            return
        }
        reconnect(true)
    }

    /**
     *
     * @param isFirstConnect 是否首次链接
     */
    override fun reconnect(isFirstConnect: Boolean) {
        if (!isFirstConnect) {
            // 非首次连接，代表之前已经进行过重连，延时一段时间再去重连
            try {
                Log.w(LOG_TAG, "非首次连接，延时${mIMSOptions?.reconnectInterval}ms再次尝试重连")
                val time=mIMSOptions?.reconnectInterval?.toLong()?:0L
                Thread.sleep(time)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        if (!isClosed.get()&&!isReconnecting.get()){
            synchronized(this){
                if (!isClosed.get()&&!isReconnecting.get()){
                    if (mIMSConnectStatus==IMSConnectStatus.Connected){
                        Log.w(LOG_TAG, "已连接，无需重连")
                        return
                    }
                    // 标识正在进行重连
                    isReconnecting.compareAndSet(false, true)
                    // 关闭channel
                    closeChannel()
                    mExecutors?.execBossTask(NettyTCPReconnectTask(getInstance()))
                }
            }
        }
    }


    override fun sendMsg(msg: IMSMsg) {
        sendMsg(msg, null, true)
    }

    override fun sendMsg(msg: IMSMsg, listener: IMSMsgSentStatusListener?) {
        sendMsg(msg, listener, true)
    }

    override fun sendMsg(msg: IMSMsg, isJoinResendManager: Boolean) {
        sendMsg(msg, null, isJoinResendManager)
    }

    override fun sendMsg(
        msg: IMSMsg?,
        listener: IMSMsgSentStatusListener?,
        isJoinResendManager: Boolean
    ) {
        if (!initialized) {
            Log.w(LOG_TAG, "IMS初始化失败，initialized is false")
            return
        }
        if (null==channel) {
            Log.w(LOG_TAG, "发送消息失败，channel为空 message= ${msg?.body ?: ""}")
            return
        }
        try {
            Log.d(LOG_TAG,"发送消息sendMsg(NettyTCPIMS.kt:177)")
            channel!!.writeAndFlush(msg)
        } catch (ex: java.lang.Exception) {
            Log.w(LOG_TAG, "发送消息失败，reason:" + ex.message + "\tmessage=" + msg)
        }
    }

    override fun release() {
        // 关闭channel
        closeChannel()
        // 关闭bootstrap
        closeBootstrap()
        //标记IM关闭
        isClosed.set(true)
        //是否在重连
        isReconnecting.set(false)
        // 标识未进行初始化
        initialized = false
        // 释放线程池组
        mExecutors?.destroy()
        mExecutors = null
        // 取消注册网络连接状态监听
        NetworkManager.getInstance().unregisterObserver(mContext, this)
    }

    /**
     * 获取由应用层构造的握手消息
     * @return
     */
   private fun getHandshakeMsg(): IMSMsg? {
        return mIMSMsgReceivedListener?.getHandshakeMsg()
    }

    /**
     * 获取由应用层构造的心跳消息
     *
     * @return
     */
    fun getHeartbeatMsg(): IMSMsg? {
        return mIMSMsgReceivedListener?.getHeartbeatMsg()
    }

    fun addHeartbeatHandler(){
        channel?.let {
            val pipeline=it.pipeline()
            if ( !it.isActive || it.pipeline() == null) {
                return
            }
            try {
                // 之前存在的读写超时handler，先移除掉，再重新添加
                if (pipeline[IdleStateHandler::class.java.simpleName] != null) {
                    pipeline.remove(IdleStateHandler::class.java.simpleName)
                }
                // 3次心跳没响应，代表连接已断开
                val heartbeatInterval=mIMSOptions?.foregroundHeartbeatInterval!!.toLong()
                pipeline.addFirst(
                    IdleStateHandler::class.java.simpleName, IdleStateHandler(
                        heartbeatInterval * 3, heartbeatInterval, 0, TimeUnit.MILLISECONDS
                    )
                )
                // 重新添加HeartbeatHandler 心跳任务管理
                if (pipeline[HeartbeatHandler::class.java.simpleName] != null) {
                    pipeline.remove(HeartbeatHandler::class.java.simpleName)
                }
                if (pipeline[NettyTCPReadHandler::class.java.simpleName] != null) {
                    pipeline.addBefore(
                        NettyTCPReadHandler::class.java.simpleName,
                        HeartbeatHandler::class.java.simpleName,
                        HeartbeatHandler(this)
                    )
                }else{}
            }catch (e: Exception){
                e.printStackTrace()
                Log.e(
                    LOG_TAG,
                    "addHeartbeatHandler(NettyTCPIMS.kt:225)添加心跳消息管理handler失败，reason：${e.message}"
                )
            }
        }

    }


    fun callbackIMSConnectStatus(connectStatus: IMSConnectStatus) {
        Log.d(LOG_TAG, "回调ims连接状态 callbackIMSConnectStatus：$connectStatus")
        if (mIMSConnectStatus == connectStatus) {
            Log.w(LOG_TAG, "连接状态与上一次相同，无需执行任何操作")
            return
        }
        mIMSConnectStatus = connectStatus
        when (connectStatus) {
            IMSConnectStatus.Unconnected -> {
                mIMSConnectStatusListener?.onUnconnected()
            }
            IMSConnectStatus.Connecting -> {
                mIMSConnectStatusListener?.onConnecting()
            }
            IMSConnectStatus.Connected -> {
                // 连接成功，发送握手消息
                val handshakeMsg = getHandshakeMsg()
                if (handshakeMsg != null) {
                    Log.d("NettyTCPIMS","发送握手消息callbackIMSConnectStatus(NettyTCPIMS.kt:277)message=${handshakeMsg}")
                    sendMsg(handshakeMsg, false)
                } else {
                    Log.e("NettyTCPIMS","callbackIMSConnectStatus(NettyTCPIMS.kt:280)请应用层构建握手消息")
                }
                mIMSConnectStatusListener?.onConnected()
            }
            else -> {
                val errCode: Int = connectStatus.errCode
                val errMsg: String = connectStatus.errMsg
                mIMSConnectStatusListener?.onConnectFailed(errCode, errMsg)
            }
        }

    }


    override fun onNetworkAvailable() {
        Log.d(LOG_TAG, "网络可用，启动ims")
        isClosed.compareAndSet(true, false)
        this.isNetworkAvailable = true
        // 网络连接时，自动重连ims
        reconnect(true)
    }

    override fun onNetworkUnavailable() {
        Log.d(LOG_TAG, "网络不可用，关闭ims")
        isClosed.compareAndSet(false, true)
        this.isNetworkAvailable = false
        isReconnecting .compareAndSet(false, false)
        // 网络断开时，销毁重连线程组（停止重连任务）
        mExecutors?.destroyBossLoopGroup()
        callbackIMSConnectStatus(IMSConnectStatus.ConnectFailed_NetworkUnavailable)
        // 关闭channel
        closeChannel()
        // 关闭bootstrap
        closeBootstrap()
    }

    private fun closeChannel() {
        try {
            channel?.let {
                removeHandler(NettyTCPReadHandler.TAG)
                it.close()
                it.eventLoop().shutdownGracefully()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            channel = null
        }
    }

    /**
     * 关闭bootstrap
     */
    private fun closeBootstrap() {
        try {
            bootstrap?.config()?.group()?.shutdownGracefully()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bootstrap = null
        }
    }

    private fun removeHandler(name: String) {
        val pipeline: ChannelPipeline? = channel?.pipeline()
        if (null != pipeline && pipeline[name] != null) {
            pipeline.remove(name)
        }
    }

    /**
     * 初始化bootstrap
     */
    fun initBootStrap() {
        closeBootstrap()
        val loopGroup: NioEventLoopGroup = NioEventLoopGroup(4)
        bootstrap = Bootstrap().apply {
            group(loopGroup)
                .channel(NioSocketChannel::class.java)
                // 设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 设置禁用nagle算法，如果要求高实时性，有数据发送时就马上发送，
                // 就将该选项设置为true关闭Nagle算法；如果要减少发送次数减少网络交互，
                // 就设置为false等累积一定大小后再发送。默认为false
                .option(ChannelOption.TCP_NODELAY, true)
                // 设置TCP发送缓冲区大小（字节数）
                .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                // 设置TCP接收缓冲区大小（字节数）
                .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                // 设置连接超时时长，单位：毫秒
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, mIMSOptions?.connectTimeout)
                // 设置初始化ChannelHandler
                .handler(NettyTCPChannelInitializerHandler(getInstance()))

        }
    }



}