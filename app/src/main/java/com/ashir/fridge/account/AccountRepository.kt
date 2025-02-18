package com.ashir.fridge.account

import com.ashir.fridge.account.pojo.User
import com.ashir.fridge.http.HttpRequests
import com.ashir.fridge.http.Result
import com.ashir.fridge.utils.GsonUtils
import com.ashir.fridge.utils.sharedprefs.SharedPrefConstants
import com.ashir.fridge.utils.sharedprefs.SharedPrefUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AccountRepository {

    suspend fun verifyUser() : Result<User> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val payload = JSONObject()
                payload.put("token",SharedPrefUtil.getAuthInstance().getString(SharedPrefConstants.TOKEN,null))
                HttpRequests.verifyUser(payload, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data  = GsonUtils.fromJsonRestResp(response, User::class.java)
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

    suspend fun getSelfUser() : Result<User> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
               HttpRequests.getSelfUser(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data  = GsonUtils.fromJsonRestResp(response, User::class.java)
                        if(data == null) {
                            continuation.resume(Result.Error("Data null"))
                        }else if(response.isSuccessful){
                            continuation.resume(Result.Success(data))
                        }else {
                            continuation.resume(Result.Error(response))
                        }
                    }
                })
            }
        }
    }
}