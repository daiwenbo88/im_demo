package com.simple.netty_lib.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.simple.netty_lib.config.IMSConfig
import com.simple.netty_lib.config.LOG_TAG

/**
 * @author: daiwenbo
 * @date: 2021/1/30-4:43 PM
 * @description:
 */
class NetworkManager : ConnectivityManager.NetworkCallback() {
    private val mObservers: MutableList<INetworkStateChangedObserver> by lazy { mutableListOf() }
    private var mNetWorkType: NetworkType? = null

    companion object {
        fun getInstance() = Helper.instance
    }

    private object Helper {
        val instance = NetworkManager()
    }

    override fun onAvailable(network: Network) {
        Log.d(LOG_TAG, "onAvailable() network = $network")
        notifyObservers(true)
    }

    override fun onLost(network: Network) {
        Log.d(LOG_TAG, "onLost() network = $network")
        notifyObservers(false)
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        if(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)){
            if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                updateNetworkType(NetworkType.Wifi)
            }else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                updateNetworkType(NetworkType.Cellular)
            }else{
                updateNetworkType(NetworkType.Other)
            }
        }
    }
    private fun updateNetworkType(networkType: NetworkType){
        if (networkType==mNetWorkType)return
        Log.d(LOG_TAG, "updateNetworkType() type = $networkType")
        mNetWorkType=networkType
    }

    fun notifyObservers(available: Boolean) {
            if (mObservers.isNullOrEmpty())return
        mObservers.forEach {
            if (available){
                it.onNetworkAvailable()
            }else{
                it.onNetworkUnavailable()
            }
        }
    }

    fun registerObserver(context: Context?, observer: INetworkStateChangedObserver?) {
        if (null == context) return
        if (null == observer) return
        if (mObservers.contains(observer)) return

        //permission.ACCESS_NETWORK_STATE
        val request = NetworkRequest.Builder().build()
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(request, this)
        mObservers.add(observer)
    }

    fun unregisterObserver(context: Context?, observer: INetworkStateChangedObserver?){
        if (null == context) return
        if (null == observer) return
        if (!mObservers.contains(observer)) return
        val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(this)
        mObservers.remove(observer)
    }

}