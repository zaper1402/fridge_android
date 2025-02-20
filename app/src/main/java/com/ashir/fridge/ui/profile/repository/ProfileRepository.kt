package com.ashir.fridge.ui.profile.repository

import com.ashir.fridge.account.AccountManager
import com.ashir.fridge.http.HttpRequests
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.profile.pojo.LogoutData
import com.ashir.fridge.utils.GsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ProfileRepository {

    suspend fun logoutUser() : Result<LogoutData> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val payload = JSONObject()
                payload.put("uid", AccountManager.uid)
                HttpRequests.logoutUser(payload, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data  = GsonUtils.fromJsonRestResp(response, LogoutData::class.java)
                        if(data != null){
                            continuation.resume(Result.Success(data))
                        }else {
                            continuation.resume(Result.Error("Data null"))
                        }
                    }
                })
            }
        }
    }
}