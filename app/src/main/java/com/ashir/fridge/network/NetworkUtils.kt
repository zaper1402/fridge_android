package com.ashir.fridge.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.ashir.fridge.FridgeApplication
import com.threemusketeers.dliverCustomer.main.network.NetworkStateManager
import com.threemusketeers.dliverCustomer.main.network.NetworkStateManager.isConnectedToInternet

object NetworkUtils {

    const val TAG = "NetworkUtils"
    const val DISCONNECTED: Short = -1
    const val NETWORK_TYPE_GSM = 16
    var publicIpAddress : String? = null

    /**
     * Fetches the network connection using connectivity manager
     *
     * @return  * -1 in case of no network  * 0 in case of unknown network  * 1 in case of wifi  * 2 in case of 2g  * 3 in case of 3g  * 4 in case of
     * 4g
     */

    fun getNetworkTypeString(): String {
        try {
            val result = NetworkType.UNKNOWN.value
            val connectivityManager = FridgeApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return result

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                NetworkStateManager.networkType?.let{
                    return@let it.value
                }
                val networkCapabilities = connectivityManager.activeNetwork ?: return NetworkType.DISCONNECTED.value
                val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return NetworkType.DISCONNECTED.value
                when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return NetworkType.WIFI.value
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return NetworkType.ETHERNET.value
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return NetworkStateManager.estimateCellularNetworkByDownBandwidth(actNw.linkDownstreamBandwidthKbps).value
                    }
                    else -> return NetworkType.DISCONNECTED.value
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.let { info ->
                        if (!info.isConnected) {
                            return NetworkType.DISCONNECTED.value
                        }
                        if (info.type == ConnectivityManager.TYPE_WIFI) {
                            return NetworkType.WIFI.value
                        }
                        return getNetworkInfoBasedOnType(info.subtype)
                    }
                }
                return NetworkType.DISCONNECTED.value
            }
        } catch (e: Error) {
            Log.d(TAG,"error , ${e.localizedMessage}")
            return NetworkType.UNKNOWN.value
        }
    }

    private fun getNetworkInfoBasedOnType(networkInfo: Int) : String {
        when (networkInfo) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN,
            TelephonyManager.NETWORK_TYPE_GSM -> return NetworkType.TWO_G.value
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> return NetworkType.THREE_G.value
            TelephonyManager.NETWORK_TYPE_LTE,
            TelephonyManager.NETWORK_TYPE_IWLAN, 19 -> return NetworkType.FOUR_G.value
            TelephonyManager.NETWORK_TYPE_NR -> return NetworkType.FIVE_G.value
            else -> {
                Log.d(TAG,"error , nwInfo = $networkInfo")
                return NetworkType.UNKNOWN.value
            }
        }
    }

    fun isConnected(): Boolean {
        return isConnectedToInternet
    }

}

enum class NetworkType(val value : String){
    DISCONNECTED("DISCONNECTED"),
    UNKNOWN("UNKNOWN"),
    WIFI("WIFI"),
    WIFI_METERED("WIFI_METERED"),
    ETHERNET("ETHERNET"),
    TWO_G("2G"),
    THREE_G("3G"),
    FOUR_G("4G"),
    FIVE_G("5G")
}