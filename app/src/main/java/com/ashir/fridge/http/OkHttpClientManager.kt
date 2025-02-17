package com.ashir.fridge.http

import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor


class OkHttpClientManager {
    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private var client: OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(logging)
        .build()

    fun get(url: String, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(callback)
    }

    fun post(url: String, json: String, callback: Callback) {
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(callback)
    }

    fun put(url: String, json: String, callback: Callback) {
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        client.newCall(request).enqueue(callback)
    }

    fun delete(url: String, json: String, callback: Callback) {
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .delete(body)
            .build()

        client.newCall(request).enqueue(callback)
    }
}