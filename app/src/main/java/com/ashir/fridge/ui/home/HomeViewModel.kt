package com.ashir.fridge.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashir.fridge.account.AccountManager
import com.ashir.fridge.account.pojo.Categories
import com.ashir.fridge.account.pojo.Product
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.home.pojo.ProductListData
import com.ashir.fridge.ui.home.repository.HomeRepository
import com.threemusketeers.dliverCustomer.main.utils.extensions.defaultValueIfNullOrEmpty
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = AccountManager.user?.name.defaultValueIfNullOrEmpty("Your Fridge")
    }
    val text: LiveData<String> = _text

    val inventoryCategoriesLiveData: LiveData<Result<Categories>>
        get() = _getInventoryCategoriesLiveData
    private val _getInventoryCategoriesLiveData = MutableLiveData<Result<Categories>>()


    val productListLiveData : LiveData<Result<ProductListData>>
        get() = _productListLiveData
    private val _productListLiveData = MutableLiveData<Result<ProductListData>>()

    val addProductLiveData : LiveData<Result<Product>>
        get() = _addProductLiveData
    private val _addProductLiveData = MutableLiveData<Result<Product>>()

    fun getInventoryCategories() {
        _getInventoryCategoriesLiveData.value = Result.InProgress()
         viewModelScope.launch {
             _getInventoryCategoriesLiveData.value = HomeRepository.getUserInventoryCategories()
         }
    }

    fun getProductList() {
        _productListLiveData.value = Result.InProgress()
        viewModelScope.launch {
            _productListLiveData.value = HomeRepository.getProductList()
        }
    }

    fun addProduct(product: JSONObject) {
        _addProductLiveData.value = Result.InProgress()
        viewModelScope.launch {
            _addProductLiveData.value = HomeRepository.addProduct(product)
        }
    }
}