package com.threemusketeers.dliverCustomer.main.network

import android.net.NetworkCapabilities
import android.util.Log
import com.ashir.fridge.network.NetworkType
import com.hike.mazuma.network.ConnectionChanged
import com.hike.mazuma.network.NetworkConnectivityListener
import com.hike.mazuma.network.NetworkInternalHelper
import java.util.concurrent.Executors

object NetworkStateManager : ConnectionChanged {

    @JvmStatic
    var isConnectedToInternet = true
        private set
    const val TAG = "NetworkStateManager"

    private val networkConnectedListeners = mutableSetOf<NetworkConnectivityListener>()
    private val singleThreadExecutor = Executors.newSingleThreadScheduledExecutor()
    private var isInitial = true
    @JvmStatic
    var networkType : NetworkType? = null
        private set

    init {
        NetworkInternalHelper(this)
        singleThreadExecutor.execute {  }
    }

    fun initializeOnStartup(){
        // already basic initialization happening on init , So do any thing extra here.
    }

    override fun networkStatus(isConnected: Boolean) {
        if(isConnectedToInternet == isConnected) return
        Log.d(TAG, "isConnected to internet is $isConnected")
        isConnectedToInternet = isConnected
        if(!isConnectedToInternet){
            networkType = null
        }
        publishStateToAllListeners(isConnected)
    }

    override fun updateNetworkCapabilites(networkCapabilities: NetworkCapabilities) {
        Log.d(TAG,"Network capabilites changed $networkCapabilities")
        when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                networkType = if(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).not()){
                    null
                }else if(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)){
                    NetworkType.WIFI
                } else {
                    NetworkType.WIFI_METERED
                }
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> networkType = NetworkType.ETHERNET
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                networkType = estimateCellularNetworkByDownBandwidth(networkCapabilities.linkDownstreamBandwidthKbps)
            }
            else -> networkType = null
        }
    }

    fun estimateCellularNetworkByDownBandwidth(bandwidth : Int) : NetworkType {
        when(bandwidth){
            in 0..100 -> return NetworkType.TWO_G
            in 101..5000 -> return NetworkType.THREE_G
            in 5001..103000 -> return NetworkType.FOUR_G
            else -> return NetworkType.FIVE_G
        }
    }

    private fun publishStateToAllListeners(isConnected: Boolean) {
        singleThreadExecutor.execute {
            for (listener in networkConnectedListeners) {
                if (isConnected) {
                    listener.onNetworkConnected()
                } else {
                    listener.onNetworkDisconnected()
                }
            }
        }
    }

    fun addConnectivityListener(listener: NetworkConnectivityListener) {
        networkConnectedListeners.add(listener)
        isInitial = false
        singleThreadExecutor.execute {
            if (isConnectedToInternet) {
                listener.onNetworkConnected(true)
            } else {
                listener.onNetworkDisconnected(true)
            }
        }
    }

    fun removeConnectivityListener(listener: NetworkConnectivityListener) = networkConnectedListeners.remove(listener)

}