package com.ashir.fridge.http

import com.ashir.fridge.account.AccountManager
import okhttp3.Callback
import org.json.JSONObject


object HttpRequests {
    val clientManager = OkHttpClientManager()

    fun loginUser(payload : JSONObject, callback: Callback) {
        clientManager.post(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"auth/login"),payload.toString(), callback)
    }

    fun verifyUser(payload : JSONObject, callback: Callback) {
        return clientManager.post(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"auth/verify"), payload.toString(), callback)
    }

    fun signUpUser(payload : JSONObject, callback: Callback) {
        return clientManager.post(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"/user/update"), payload.toString(), callback)
    }

    fun getSelfUser(callback: Callback) {
        return clientManager.get(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"/user/get?uid=${AccountManager.uid}"), callback)
    }

    fun getUserInventoryCategories(callback: Callback) {
        val userId = AccountManager.uid
        return clientManager.get(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"/user/get-all-categories?user_id=${userId}"), callback)
    }

    fun getProductByCategory(category: String, callback: Callback) {
        val userId = AccountManager.uid
        return clientManager.get(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"/user/get-products?category=${category}&user_id=${userId}"), callback)
    }

    fun getProductList(callback: Callback) {
        return clientManager.get(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"/product/get-product-list"), callback)
    }

    fun addProduct(payload : JSONObject, callback: Callback) {
        return clientManager.post(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"/user/addProduct"), payload.toString(), callback)
    }

    fun updateProduct(payload :String, callback: Callback) {
        return clientManager.post(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"/user/update-products"), payload, callback)
    }

    fun getNotifData(callback: Callback) {
        return clientManager.get(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"/user/get-notifications?uid=${AccountManager.uid}"), callback)
    }

    fun logoutUser(payload : JSONObject, callback: Callback) {
        return clientManager.post(HttpDomainManager.generateUrl(DomainType.BASE_DOMAIN,"auth/logout"), payload.toString(), callback)
    }
}