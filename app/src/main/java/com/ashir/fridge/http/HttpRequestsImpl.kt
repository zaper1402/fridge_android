package com.ashir.fridge.http

import android.util.Log
import com.ashir.fridge.FridgeApplication
import com.ashir.fridge.utils.sharedprefs.SharedPrefConstants
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import com.threemusketeers.dliverCustomer.main.utils.extensions.defaultValueIfNull
import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProvider
import org.chromium.net.UrlRequest
import org.chromium.net.apihelpers.UploadDataProviders
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors


object HttpRequestsImpl {
    private val cronetEngine: CronetEngine  by lazy{ CronetEngine.Builder(FridgeApplication.instance).build()}

    private var executor: Executor = Executors.newSingleThreadExecutor()
    const val TAG = "HttpRequestsImpl"
    private fun makeRequestBuilder(url: String?, httpMethod: String?, callback: UrlRequest.Callback?): UrlRequest.Builder {
        val requestBuilder: UrlRequest.Builder = cronetEngine.newUrlRequestBuilder(url, callback, executor)
        requestBuilder.setHttpMethod(httpMethod)
        val authPref: SharedPrefUtil = SharedPrefUtil.getAuthInstance()
        val accessToken = authPref.getString(SharedPrefConstants.TOKEN, "null")
        val client = authPref.getString("client", "null")
        val expiry = authPref.getLong("expiry", 0).toString()
//        val uid = AccountManager.customer?.id?:"null"
        requestBuilder.addHeader("Content-Type", "application/json")
        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        requestBuilder.addHeader("client", client)
//        requestBuilder.addHeader("uid", uid)
        requestBuilder.addHeader("expiry", expiry)
        Log.i(TAG, "API Req-url: $url")
        return requestBuilder
    }


    private fun generateUploadDataProvider(payload: String): UploadDataProvider {
        val bytes = convertStringToBytes(payload)
        return UploadDataProviders.create(bytes)
    }


    private fun convertStringToBytes(payload: String): ByteArray {
        val bytes: ByteArray
        val byteBuffer = ByteBuffer.wrap(payload.toByteArray())
        if (byteBuffer.hasArray()) {
            bytes = byteBuffer.array()
        } else {
            bytes = ByteArray(byteBuffer.remaining())
            byteBuffer[bytes]
        }
        return bytes
    }


    fun get(url: String?, callback: UrlRequest.Callback?): UrlRequest? {
        val requestBuilder = makeRequestBuilder(url, RequestTypes.GET.name, callback)
        return requestBuilder.build()
    }


    fun put(url: String?, payload: String?, callback: UrlRequest.Callback?): UrlRequest? {
        val requestBuilder = makeRequestBuilder(url, RequestTypes.PUT.name, callback)
        requestBuilder.setUploadDataProvider(generateUploadDataProvider(payload.defaultValueIfNull()), executor)
        return requestBuilder.build()

    }

    fun post(url: String?, payload: String?, callback: UrlRequest.Callback?): UrlRequest? {
        val requestBuilder = makeRequestBuilder(url, RequestTypes.POST.name, callback)
        requestBuilder.setUploadDataProvider(generateUploadDataProvider(payload.defaultValueIfNull()), executor)
        return requestBuilder.build()
    }

    fun delete(url: String?, payload: String?, callback: UrlRequest.Callback?): UrlRequest? {
        val requestBuilder = makeRequestBuilder(url, RequestTypes.DELETE.name, callback)
        requestBuilder.setUploadDataProvider(generateUploadDataProvider(payload!!), executor)
        return requestBuilder.build()
    }

    enum class RequestTypes{
        GET,
        PUT,
        POST,
        DELETE
    }
}