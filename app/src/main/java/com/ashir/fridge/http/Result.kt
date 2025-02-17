package com.ashir.fridge.http

import org.json.JSONObject

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error<out T : Any>(val responseBody: T, val otherData : JSONObject? = null) : Result<Nothing>()
    class InProgress : Result<Nothing>()
}