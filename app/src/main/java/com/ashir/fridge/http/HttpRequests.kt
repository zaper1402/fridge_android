package com.ashir.fridge.http

import okhttp3.Callback
import org.chromium.net.UrlRequest
import org.json.JSONObject


object HttpRequests {
    val clientManager = OkHttpClientManager()

    fun loginUser(payload : JSONObject, callback: CronetCallback): UrlRequest? {
        return HttpRequestsImpl.post(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"auth/login"),payload.toString(), callback)
    }

    fun verifyUser(payload : JSONObject, callback: Callback) {
        return clientManager.post(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"auth/verify"), payload.toString(), callback)
    }
}