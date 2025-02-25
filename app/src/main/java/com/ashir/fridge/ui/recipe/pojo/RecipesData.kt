package com.ashir.fridge.ui.recipe.pojo

import com.ashir.fridge.utils.IModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Recipes(
    @SerializedName("breakfast_recipes") val breakfastRecipes: List<RecipesData>? = null,
    @SerializedName("lunch_recipes") val lunchRecipes: List<RecipesData>? = null,
    @SerializedName("dinner_recipes") val dinnerRecipes: List<RecipesData>? = null,
): Serializable, IModel

data class RecipesData(
    @SerializedName("recipe") val recipeInfo: RecipeInfo? = null,
    @SerializedName("matching_ingredients") val matchingIngredients: Int? = null,
    @SerializedName("total_ingredients") val totalIngredients: Int? = null,
): Serializable, IModel

data class RecipeInfo(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("time_to_cook") val ttc: Int? = null,
    @SerializedName("difficulty") val difficulty: String? = null,
    @SerializedName("servings") val servings: Int? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("cuisine_tags") val cuisineTags: List<String>? = null,
    @SerializedName("cuisines") val cuisines: List<String>? = null,
    @SerializedName("ingredients") val ingredients: List<IngredientInfo>? = null,
    @SerializedName("instructions") val instructions: List<String>? = null,
    @SerializedName("favourite") val favourite: Boolean? = null,
): Serializable, IModel

data class IngredientInfo(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
): Serializable, IModel

data class RecipesDataList(val recipesDataList: List<RecipesData>? = null): Serializable, IModel
