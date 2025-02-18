package com.ashir.fridge.account.pojo

import com.ashir.fridge.utils.IModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//
//class Categories(models.TextChoices):
//    FRUIT = 'Fruit'
//VEGETABLE = 'Vegetable'
//BAKERY =  'Bakery'
//OIL = 'Oil'
//CANNED_FOOD = 'Canned Food'
//SAUCES = 'Sauces'
//CEREAL = 'Cereal'
//CONDIMENT = 'Condiment'
//DAIRY = 'Dairy'
//DRINK = 'Drink'
//DRY_GOODS = 'Dry Goods'
//FROZEN_FOOD = 'Frozen Food'
//MEAT = 'Meat'
//PASTA = 'Pasta'
//DRY_FRUITS = 'Dry Fruits'
//SNACKS = 'Snacks'
//SPICES = 'Spices'
//HERBS = 'Herbs'
//READY_TO_EAT_MEALS = 'Ready-to-eat Meals'
//SEAFOOD = 'Seafood'
//DESSERTS = 'Desserts'
//PICKLED_ITEMS = 'Pickled Items'
//NON_ALCOHOLIC_DRINKS = 'Non-alcoholic Drinks'
//BAKING_INGREDIENTS = 'Baking Ingredients'
//OTHER = 'Other'


data class  HomeProductCategories(
    val categories: List<HomeProductCategory>
) : IModel

data class HomeProductCategory(
    @SerializedName("product_category") val productCategory: String
) : IModel, Serializable

enum class Categories(val value: String) {
    FRUITS("Fruits"),
    VEGETABLES("Vegetables"),
    BAKERY("Bakery"),
    OIL("Oil"),
    CANNED_FOOD("Canned Food"),
    SAUCES("Sauces"),
    CEREAL("Cereal"),
    CONDIMENTS("Condiments"),
    DAIRY("Dairy"),
    DRINK("Drink"),
    DRY_GOODS("Dry Goods"),
    FROZEN_FOOD("Frozen Food"),
    MEAT("Meat"),
    PASTA("Pasta"),
    DRY_FRUITS("Dry Fruits"),
    SNACKS("Snacks"),
    SPICES("Spices"),
    HERBS("Herbs"),
    READY_TO_EAT_MEALS("Ready-to-eat Meals"),
    SEAFOOD("Seafood"),
    DESSERTS("Desserts"),
    PICKLED_ITEMS("Pickled Items"),
    NON_ALCOHOLIC_DRINKS("Non-alcoholic Drinks"),
    BAKING_INGREDIENTS("Baking Ingredients"),
    OTHER("Other")

}

enum class QuantityType(val value: String) {
    KG("Kilogram"),
    GRAM("Gram"),
    LITRE("Litre"),
    ML("Millilitre"),
    PIECE("Piece"),
}

enum class AllergyTags(val value: String) {
    DAIRY("Dairy"),
    NUTS("Nuts"),
    GLUTEN("Gluten"),
    SOY("Soy"),
    SHELLFISH("Shellfish"),
    EGG("Egg"),
    NONE("None")
}