package com.ashir.fridge.ui.home.repository

import com.ashir.fridge.account.pojo.Categories
import com.ashir.fridge.account.pojo.Product
import com.ashir.fridge.http.HttpRequests
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.home.pojo.ProductListData
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

object HomeRepository {

    suspend fun getUserInventoryCategories() : Result<Categories> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                HttpRequests.getSelfUser(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = GsonUtils.fromJson(response.body?.string(), Categories::class.java)
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

    suspend fun getProductList() : Result<ProductListData> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                HttpRequests.getProductList(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = GsonUtils.fromJsonRestResp(response, ProductListData::class.java)
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

    suspend fun addProduct(product: JSONObject) : Result<Product> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                HttpRequests.addProduct(product, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = GsonUtils.fromJsonRestResp(response, Product::class.java)
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