package com.ashir.fridge.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashir.fridge.account.AccountManager
import com.ashir.fridge.account.pojo.HomeProductCategories
import com.ashir.fridge.account.pojo.Product
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.home.pojo.ProductListData
import com.ashir.fridge.ui.home.pojo.UserProduct
import com.ashir.fridge.ui.home.pojo.UserProductData
import com.ashir.fridge.ui.home.repository.HomeRepository
import com.threemusketeers.dliverCustomer.main.utils.extensions.defaultValueIfNullOrEmpty
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = AccountManager.user?.name.defaultValueIfNullOrEmpty("Your Fridge")
    }
    val text: LiveData<String> = _text

    val inventoryCategoriesLiveData: LiveData<Result<HomeProductCategories>>
        get() = _getInventoryCategoriesLiveData
    private val _getInventoryCategoriesLiveData = MutableLiveData<Result<HomeProductCategories>>()

    val categoryProductLiveData : LiveData<Result<UserProductData>>
        get() = _categoryProductLiveData
    private val _categoryProductLiveData = MutableLiveData<Result<UserProductData>>()

    val productListLiveData : LiveData<Result<ProductListData>>
        get() = _productListLiveData
    private val _productListLiveData = MutableLiveData<Result<ProductListData>>()

    val addProductLiveData : LiveData<Result<Product>>
        get() = _addProductLiveData
    private val _addProductLiveData = MutableLiveData<Result<Product>>()

    val updateProductLiveData : LiveData<Result<Product>>
        get() = _updateProductLiveData
    private val _updateProductLiveData = MutableLiveData<Result<Product>>()

    fun getInventoryCategories() {
        _getInventoryCategoriesLiveData.value = Result.InProgress()
         viewModelScope.launch {
             _getInventoryCategoriesLiveData.value = HomeRepository.getUserInventoryCategories()
         }
    }

    fun getProductByCategory(category: String){
        _categoryProductLiveData.value = Result.InProgress()
        viewModelScope.launch {
            _categoryProductLiveData.value = HomeRepository.getProductByCategory(category)
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

    fun updateProduct(product: List<UserProduct>) {
        _updateProductLiveData.value = Result.InProgress()
        viewModelScope.launch {
            _updateProductLiveData.value = HomeRepository.updateProduct(product)
        }
    }
}