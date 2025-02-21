package com.ashir.fridge.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ashir.fridge.R
import com.ashir.fridge.account.pojo.HomeProductCategory
import com.ashir.fridge.databinding.CategoryProductsLayoutBinding
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.home.HomeViewModel
import com.ashir.fridge.ui.home.adapters.CategoryProductsAdapter
import com.ashir.fridge.ui.home.pojo.UserProduct
import com.ashir.fridge.ui.home.pojo.UserProductData
import com.ashir.fridge.utils.IModel
import com.ashir.fridge.utils.listeners.DelegateClickListener
import com.threemusketeers.dliverCustomer.main.utils.ViewUtils
import com.threemusketeers.dliverCustomer.main.utils.extensions.setGone
import com.threemusketeers.dliverCustomer.main.utils.extensions.setVisible

class CategoryProductFragment : Fragment() {

    private var _binding: CategoryProductsLayoutBinding? = null
    private val binding get() = _binding!!
    private var categoryData: HomeProductCategory? = null
    private var productData : UserProductData? = null
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()

    private val productClickListener = object : DelegateClickListener {

        override fun onClick(iModel: IModel?, position: Int, otherData: Any?) {
            when (iModel) {
                is CategoryProductsAdapter.ProductGroupedData -> {
                    openChildFragment(ProductEntriesFragment.newInstance(iModel.userProducts as ArrayList<UserProduct>), SearchProductFragment.TAG, true)

                }
            }
        }
    }

    private val categoryProductsAdapter = CategoryProductsAdapter(productClickListener)

    companion object {
        const val TAG = "CategoryProductFragment"

        fun newInstance(category: HomeProductCategory) = CategoryProductFragment().apply {
            arguments = Bundle().apply {
                // put arguments here
                putSerializable("category", category)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get arguments here
        categoryData = arguments?.getSerializable("category") as HomeProductCategory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CategoryProductsLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // use categoryData here
        setupUI()
        setupObservers()
        homeViewModel.getProductByCategory(categoryData?.productCategory ?: "")
        setupClickListeners()
    }

    private fun setupUI() {
        // setup UI here
        binding.header.text = categoryData?.productCategory
        setupAdapter()
    }

    private fun setupAdapter() {
        // setup adapter here
        binding.categoryProductsRv.adapter = categoryProductsAdapter
        categoryProductsAdapter.submitList(emptyList())
    }

    private fun setupObservers() {
        // setup observers here
        homeViewModel.categoryProductLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Result.InProgress -> {
                    setLoadingState()
                }
                is Result.Success -> {
                    // hide progress

                    setSuccessState(it.data)
                }
                is Result.Error<*> -> {
                    // hide progress
                    // show error
                    setErrorState()
                    ViewUtils.showGenericErrorToast(requireContext())
                }
            }
        }
    }

    private fun processProductDataForRv(data : UserProductData) {
        // group product by product.name
        val groupedData = mutableListOf<CategoryProductsAdapter.ProductGroupedData>()
        val productMap = mutableMapOf<String, MutableList<UserProduct>>()
        for(product in data.inventory) {
            if(productMap.containsKey(product.name)) {
                productMap[product.name]?.add(product)
            }else {
                productMap[product.name] = mutableListOf(product)
            }
        }

        for((key, value) in productMap) {
            val totalQuantity = value.sumOf { it.quantity.toInt() }
            groupedData.add(CategoryProductsAdapter.ProductGroupedData(key, totalQuantity, value))
        }
        categoryProductsAdapter.submitList(groupedData)
    }

    private fun setupClickListeners() {
        // setup click listeners here
        binding.backIc.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.searchIc.setOnClickListener {
            // open search fragment
        }
    }

    private fun setSuccessState(data: UserProductData) {
        // Set Success State
        binding.categoryProductsRv.setVisible()
        binding.emptyString.setGone()
        productData = data
        processProductDataForRv(data)
    }

    private fun setLoadingState() {
        // Set Loading State
        binding.categoryProductsRv.setGone()
        binding.emptyString.text = getString(R.string.please_wait_fetching_all_your_items)
        binding.emptyString.setVisible()
    }

    private fun setErrorState() {
        // Set Error State
        binding.categoryProductsRv.setGone()
        binding.emptyString.text = getString(R.string.uh_oh_something_went_wrong_please_try_again_later)
        binding.emptyString.setVisible()
    }

    private fun openChildFragment(fragment: Fragment,tag : String, addtoBackstack : Boolean) {
        val fragmentObj = parentFragmentManager.beginTransaction().replace(R.id.fragment_container_view, fragment)
        if(addtoBackstack) {
            fragmentObj.addToBackStack(tag)
        }
        fragmentObj.commitAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


