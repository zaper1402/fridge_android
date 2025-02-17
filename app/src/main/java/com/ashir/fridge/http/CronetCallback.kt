package com.ashir.fridge.http

import android.util.Log
import com.ashir.fridge.utils.GsonUtils
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels


abstract class CronetCallback : UrlRequest.Callback() {

    var headers: String? = null
    var responseBody: JSONObject? = null
    var httpStatusCode = 0
    private val BYTE_BUFFER_CAPACITY_BYTES = 102400
    private val bytesReceived = ByteArrayOutputStream()
    private val receiveChannel = Channels.newChannel(bytesReceived)


    final override fun onRedirectReceived(p0: UrlRequest, p1: UrlResponseInfo, p2: String) {
        p0.followRedirect();
    }

    final override fun onResponseStarted(p0: UrlRequest, p1: UrlResponseInfo) {
        Log.i(HttpRequestsImpl.TAG, "On Response Started: ${p1.url}")
        p0.read(ByteBuffer.allocateDirect(BYTE_BUFFER_CAPACITY_BYTES))
    }

    final override fun onReadCompleted(p0: UrlRequest, p1: UrlResponseInfo, p2: ByteBuffer) {
        p0.read(p2)
        val statusCode: Int = p1.httpStatusCode
        this.httpStatusCode = statusCode

        val bytes: ByteArray
        if (p2.hasArray()) {
            bytes = p2.array()
        } else {
            bytes = ByteArray(p2.remaining())
            p2.get(bytes)
        }

        var responseBodyString = String(bytes) //Convert bytes to string
        responseBodyString = responseBodyString.trim { it <= ' ' }.replace(Regex("\r\n|\n\r|\r|\n|\r0|\n0"), "")
        if (responseBodyString.endsWith("0")) {
            responseBodyString = responseBodyString.substring(0, responseBodyString.length - 1)
        }

        val headers: Map<String, List<String>> = p1.allHeaders //get headers
        this.headers = createHeaders(headers)

        this.responseBody = GsonUtils.getJSONObject(responseBodyString)
    }

    final override fun onSucceeded(p0: UrlRequest, p1: UrlResponseInfo) {
        Log.i(HttpRequestsImpl.TAG, "On Request Processing Succeeded: ${p1.httpStatusCode}, resp: $responseBody ")
        if(p1.httpStatusCode < 399){
            onSucceeded(p0, p1, ResponseBody(statusCode = p1.httpStatusCode,responseBody))
        }else {
            onFailed(p0, p1, ResponseBody(statusCode = p1.httpStatusCode,responseBody))
        }
    }

    final override fun onFailed(p0: UrlRequest?, p1: UrlResponseInfo?, p2: CronetException?) {
        val inform = "CronetExceptionError: failed with status code -  ${p1?.httpStatusCode.toString()}. Caused by: ${p2?.localizedMessage} ${p1?.httpStatusText}."
        val results = JSONObject()
        try {
            results.put("headers", p1?.allHeaders?.let { createHeaders(it) })
            results.put("body", inform)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(HttpRequestsImpl.TAG, "On Request Processing Failed: ${p1?.httpStatusCode}, resp: $responseBody ")
        onFailed(p0,p1, ResponseBody(p1?.httpStatusCode,results))
    }

    final override fun onCanceled(p0: UrlRequest, p1: UrlResponseInfo?) {
        val bodyBytes = bytesReceived.toByteArray()
        onCancelled(p0,p1,bodyBytes)
    }

    private fun createHeaders(headers: Map<String, List<String>>): String {
        var accessToken = "null"
        var client = "null"
        var uid = "null"
        var expiry: Long = 0
        if (headers.containsKey("Access-Token")) {
            val accTok = headers["Access-Token"]!!
            if (accTok.isNotEmpty()) {
                accessToken = accTok[accTok.size - 1]
            }
        }
        if (headers.containsKey("Client")) {
            val cl = headers["Client"]!!
            if (cl.isNotEmpty()) {
                client = cl[cl.size - 1]
            }
        }
        if (headers.containsKey("Uid")) {
            val u = headers["Uid"]!!
            if (u.isNotEmpty()) {
                uid = u[u.size - 1]
            }
        }
        if (headers.containsKey("Expiry")) {
            val ex = headers["Expiry"]!!
            if (ex.isNotEmpty()) {
                expiry = ex[ex.size - 1].toLong()
            }
        }
        val currentHeaders = JSONObject()
        try {
            currentHeaders.put("access-token", accessToken)
            currentHeaders.put("client", client)
            currentHeaders.put("uid", uid)
            currentHeaders.put("expiry", expiry)
            return currentHeaders.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return currentHeaders.toString()
    }

    abstract fun onSucceeded(request: UrlRequest, info: UrlResponseInfo, responseBody : ResponseBody)

    abstract fun onFailed(request: UrlRequest?, info: UrlResponseInfo?,responseBody: ResponseBody)

    abstract fun onCancelled(request: UrlRequest, info: UrlResponseInfo?, bodyBytes: ByteArray)

    data class ResponseBody(val statusCode: Int?, val content: Any?)

}