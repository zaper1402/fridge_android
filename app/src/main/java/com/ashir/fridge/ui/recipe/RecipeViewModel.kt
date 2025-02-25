package com.ashir.fridge.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.home.repository.HomeRepository
import com.ashir.fridge.ui.recipe.pojo.Recipes
import com.ashir.fridge.ui.recipe.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    val recipesLiveData: LiveData<Result<Recipes>>
        get() = _getRecipesLiveData
    private val _getRecipesLiveData = MutableLiveData<Result<Recipes>>()

    fun getRecipes(cuisineId:String?) {
        _getRecipesLiveData.value = Result.InProgress()
        viewModelScope.launch {
            _getRecipesLiveData.value = RecipeRepository.getRecipesForCuisine(cuisineId)
        }
    }
}