package com.ashir.fridge.ui.home.repository

import com.ashir.fridge.account.AccountManager
import com.ashir.fridge.account.pojo.HomeProductCategories
import com.ashir.fridge.account.pojo.Product
import com.ashir.fridge.http.HttpRequests
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.home.pojo.ProductListData
import com.ashir.fridge.ui.home.pojo.UserProduct
import com.ashir.fridge.ui.home.pojo.UserProductData
import com.ashir.fridge.utils.GsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object HomeRepository {

    suspend fun getUserInventoryCategories() : Result<HomeProductCategories> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                HttpRequests.getUserInventoryCategories(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = GsonUtils.fromJsonRestResp(response, HomeProductCategories::class.java)
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

    suspend fun getProductByCategory(category: String): Result<UserProductData> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                HttpRequests.getProductByCategory(category, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = GsonUtils.fromJsonRestResp(response, UserProductData::class.java)
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

    suspend fun updateProduct(product: List<UserProduct>) : Result<Product> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val payload = JSONArray()
                for (userProduct in product) {
                    val data = GsonUtils.toJson(userProduct)
                    data?.put("quantity", userProduct.currentQuantity)
                    payload.put(data)
                }
                val productPayload = JSONObject()
                productPayload.put("user_products", payload)
                productPayload.put("user_id", AccountManager.uid)
                HttpRequests.updateProduct(productPayload.toString(), object : Callback {
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