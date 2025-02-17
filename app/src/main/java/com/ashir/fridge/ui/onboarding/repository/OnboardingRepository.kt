package com.ashir.fridge.ui.onboarding.repository

import com.ashir.fridge.account.pojo.User
import com.ashir.fridge.http.HttpRequests
import com.ashir.fridge.http.Result
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

object OnboardingRepository {

    suspend fun loginUser(email : String, password: String) : Result<User> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val payload = JSONObject()
                payload.put("email", email)
                payload.put("password",password)
                HttpRequests.loginUser(payload, object : Callback {
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

    suspend fun signUpUser(form:HashMap<String,String>) : Result<User> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val payload = JSONObject()
                form.forEach { (key, value) ->
                    payload.put(key, value)
                }
                HttpRequests.signUpUser(payload, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = GsonUtils.fromJsonRestResp(response, User::class.java)
                        if (data != null) {
                            continuation.resume(Result.Success(data))
                        } else {
                            continuation.resume(Result.Error("Data null"))
                        }
                    }
                })
            }
        }
    }
}