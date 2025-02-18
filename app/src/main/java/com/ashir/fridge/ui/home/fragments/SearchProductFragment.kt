package com.ashir.fridge.ui.home.fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ashir.fridge.account.pojo.Product
import com.ashir.fridge.databinding.SearchProductLayoutBinding
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.home.HomeViewModel

class SearchProductFragment: Fragment() {
    private lateinit var binding: SearchProductLayoutBinding
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()
    private val items = mutableListOf<String>()
    private var data = mutableListOf<Product>()
    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, items)
    }


    companion object {
        const val TAG = "SearchProductFragment"

        fun newInstance(searchQuery: String) = SearchProductFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchProductLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getProductList()
        setupObservers()
        setupUi()
        setupClickListeners()
    }

    private fun setupUi() {
        // Setup UI
        binding.autoCompleteTextView.setAdapter(adapter)
        binding.autoCompleteTextView.threshold = 1

    }

    private fun setupObservers() {
        // Setup Observers
        homeViewModel.productListLiveData.observe(viewLifecycleOwner) { resp ->
            when (resp) {
                is Result.Success -> {
                    // Handle Success
                    data = resp.data.products.toMutableList()
                    adapter.clear()
                    adapter.addAll(resp.data.products.map { it.name })
                }

                is Result.Error<*> -> {
                    // Handle Error
                }

                is Result.InProgress -> {
                    // Handle Loading
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Setup Click Listeners
        binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val item = data[position]
            // Handle Item Click
            parentFragmentManager.beginTransaction()
                .replace(com.ashir.fridge.R.id.fragment_container_view, ProductFormFragment.newInstance(product = item)).addToBackStack(ProductFormFragment.TAG)
                .commitAllowingStateLoss()

        }
    }

}