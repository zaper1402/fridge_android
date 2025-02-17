package com.ashir.fridge.ui.onboarding.repository

import com.ashir.fridge.account.pojo.User
import com.ashir.fridge.http.CronetCallback
import com.ashir.fridge.http.HttpRequests
import com.ashir.fridge.http.Result
import com.ashir.fridge.utils.GsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object OnboardingRepository {

    suspend fun loginUser(email : String, password: String) : Result<User> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val payload = JSONObject()
                payload.put("email", email)
                payload.put("password",password)
                val request = HttpRequests.loginUser(payload, object : CronetCallback() {
                    override fun onSucceeded(request: UrlRequest, info: UrlResponseInfo, responseBody: ResponseBody) {
                        val data  = GsonUtils.fromJsonRestResp(responseBody, User::class.java)
                        if(data != null){
                            continuation.resume(Result.Success(data))
                        }else {
                            continuation.resume(Result.Error("Data null"))
                        }
                    }

                    override fun onFailed(request: UrlRequest?, info: UrlResponseInfo?, responseBody: ResponseBody) {
                        continuation.resume(Result.Error(responseBody))
                    }

                    override fun onCancelled(request: UrlRequest, info: UrlResponseInfo?, bodyBytes: ByteArray) {
                        continuation.resume(Result.Error(ResponseBody(-1,"Cancelled")))
                    }
                })
                request?.start()
            }
        }
    }
}