package com.ashir.fridge.ui.recipe.repository

import com.ashir.fridge.account.pojo.HomeProductCategories
import com.ashir.fridge.http.HttpRequests
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.recipe.pojo.Recipes
import com.ashir.fridge.utils.GsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object RecipeRepository {

    suspend fun getRecipesForCuisine(cuisineId: String?) : Result<Recipes> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                HttpRequests.getRecipesByCuisine(cuisineId, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        continuation.resume(Result.Error(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val data = GsonUtils.fromJsonRestResp(response, Recipes::class.java)
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