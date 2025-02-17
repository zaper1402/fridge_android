package com.hike.mazuma.network

import androidx.annotation.WorkerThread

interface NetworkConnectivityListener {
    /**
     * This will be invoked when network available and when this got registered first time from [com.hike.mazuma.HikeApplication]
     * User Worker thread to perform operation this callback
     */
    @WorkerThread
    fun onNetworkConnected(isInitial:Boolean = false)

    /**
     * This will be invoked when network not available
     * User Worker thread to perform operation this callback
     */
    @WorkerThread
    fun onNetworkDisconnected(isInitial:Boolean = false)
}