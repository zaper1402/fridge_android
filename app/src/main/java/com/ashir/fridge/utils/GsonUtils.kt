package com.ashir.fridge.utils

import android.util.Log
import com.ashir.fridge.http.CronetCallback
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.threemusketeers.dliverCustomer.main.utils.extensions.toJSONObject
import okhttp3.Response
import org.json.JSONObject

object GsonUtils {
    private val gson = Gson()
    private var gsonPretty = GsonBuilder().setPrettyPrinting().create()

    fun <T> fromJson(jsonString: String?, classType: Class<T>): T? {
        try {
            if (jsonString == null) return null
            return gson.fromJson<T>(jsonString, classType)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    fun <T> fromJson(response: CronetCallback.ResponseBody?, classType: Class<T>): T? {
        return try {
            fromJson((response?.content as? JSONObject).toString(), classType)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> fromJsonRestResp(response: CronetCallback.ResponseBody?, classType: Class<T>): T? {
        return try {
            val jsonString = response?.content.toString()
            Gson().fromJson(jsonString, classType)
        } catch (e: Exception) {
            Log.e("GsonUtils", "fromJsonRestResp: ${e.message}")
            null
        }
    }

    fun <T> fromJsonRestResp(response: Response?, classType: Class<T>): T? {
        return try {
            val jsonString = response?.body?.string()
            Gson().fromJson(jsonString, classType)
        } catch (e: Exception) {
            Log.e("GsonUtils", "fromJsonRestResp: ${e.message}")
            null
        }
    }

    fun getJSONObject(jsonString: String?): JSONObject {
        if(jsonString.isNullOrBlank()) return JSONObject()
        return try {
            JSONObject(jsonString)
        } catch (e: Exception) { // in case of invalid jsonString return default JSONObject
            e.printStackTrace()
            JSONObject()
        }

    }

    fun toJsonString(obj: Any?): String? {
        return gson.toJson(obj)
    }

    fun toJson(obj: Any?): JSONObject? {
        return gson.toJson(obj).toJSONObject()
    }


    fun <T> toJsonArray(list: List<T>): String? {
        return try {
            gson.toJson(list, object : TypeToken<List<T>>() {}.type)
        } catch (e: Exception) {
            Log.e("GsonUtils", "toJsonArray: ${e.message}")
            null
        }
    }


}