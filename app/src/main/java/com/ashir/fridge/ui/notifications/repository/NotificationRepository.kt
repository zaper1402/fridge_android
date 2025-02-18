package com.ashir.fridge.ui.notifications.repository

import com.ashir.fridge.http.HttpRequests
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.notifications.pojo.NotifData
import com.ashir.fridge.utils.GsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NotificationRepository {

    suspend fun getNotifData() : Result<NotifData> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                HttpRequests.getNotifData(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = GsonUtils.fromJsonRestResp(response, NotifData::class.java)
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