package com.hike.mazuma.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import com.threemusketeers.dliverCustomer.main.utils.extensions.getAppContext

class NetworkInternalHelper(val connectionChanged: ConnectionChanged) {

    private val mContext = getAppContext()
    private var isOnline = false
    private var connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var callback = ConnectionStatusCallback()


    init {
        isOnline = getInitialConnectionStatus()
        sendEvent(isOnline)
        try {
            connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
        } catch (e: Error) {
            Log.w(this.javaClass.name, "NetworkCallback for Wi-fi was not registered or already unregistered")
        }
    }

    private fun getInitialConnectionStatus(): Boolean {
        try {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
            } else {
                val activeNetwork = connectivityManager.getActiveNetworkInfo() // Deprecated in 29
                activeNetwork != null && activeNetwork.isConnectedOrConnecting // // Deprecated in 28
            }
        } catch (e:Error) {
            return false
        }
    }

    private fun sendEvent(status: Boolean) {
        connectionChanged.networkStatus(status)
    }

    inner class ConnectionStatusCallback : ConnectivityManager.NetworkCallback() {

        private val activeNetworks: MutableList<Network> = mutableListOf()

        override fun onLost(network: Network) {
            super.onLost(network)
            activeNetworks.removeAll { activeNetwork -> activeNetwork == network }
            isOnline = activeNetworks.isNotEmpty()
            sendEvent(isOnline)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (activeNetworks.none { activeNetwork -> activeNetwork == network }) {
                activeNetworks.add(network)
            }
            isOnline = activeNetworks.isNotEmpty()
            sendEvent(isOnline)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                connectionChanged.updateNetworkCapabilites(networkCapabilities)
            }
        }
    }
}

interface ConnectionChanged {
    fun networkStatus(isConnected:Boolean)
    fun updateNetworkCapabilites(networkCapabilities: NetworkCapabilities)
}