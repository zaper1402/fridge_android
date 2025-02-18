package com.ashir.fridge.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ashir.fridge.R
import com.ashir.fridge.databinding.ProductEditLayoutBinding
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.home.HomeViewModel
import com.ashir.fridge.ui.home.adapters.ProductsEditAdapter
import com.ashir.fridge.ui.home.pojo.UserProduct
import com.ashir.fridge.utils.IModel
import com.ashir.fridge.utils.listeners.DelegateClickListener
import com.threemusketeers.dliverCustomer.main.utils.ViewUtils
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.observeOnce

class ProductEntriesFragment : Fragment() {

    private var _binding: ProductEditLayoutBinding? = null
    private val binding get() = _binding!!
    private var productList : MutableList<UserProduct>? = null
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()

    private val delegateClickListener = object : DelegateClickListener {

        override fun onClick(iModel: IModel?, position: Int, otherData: Any?) {
            when(iModel) {
                is UserProduct -> {
                    openChildFragment(EntryUpdateFragment.newInstance(iModel), EntryUpdateFragment.TAG, true)
                }
            }
        }
    }
    private val adapter: ProductsEditAdapter = ProductsEditAdapter(delegateClickListener)

    companion object {
        const val TAG = "ProductEntriesFragment"

        fun newInstance(data : ArrayList<UserProduct>) = ProductEntriesFragment().apply {
            arguments = Bundle().apply {
                // put arguments here
                putSerializable("productList", data)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get arguments here
        productList = (arguments?.getSerializable("productList")) as? ArrayList<UserProduct>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProductEditLayoutBinding.inflate(inflater, container, false)
        binding.productsRecycler.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productList?.let { adapter.submitList(it) }
        setupUI()
        setupObserver()
        setupClickListeners()
    }

    private fun setupUI() {
        // setup UI here
        binding.header.text = getString(R.string.product, productList?.firstOrNull()?.product?.name ?: "")
    }

    private fun setupObserver() {
        // setup observer here
        homeViewModel.updateProductLiveData.observeOnce(viewLifecycleOwner) {
            // handle update product live data here
            when (it) {
                is Result.InProgress -> {
                    Toast.makeText(requireContext(), "Updating product", Toast.LENGTH_SHORT).show()

                }

                is Result.Success -> {
                    // handle success state
                    Toast.makeText(requireContext(), "Products updated", Toast.LENGTH_SHORT).show()
                    activity?.onBackPressed()
                }

                is Result.Error<*> -> {
                    // handle error state
                    ViewUtils.showGenericErrorToast(requireContext())
                }
            }
        }
    }

    private fun setupClickListeners() {
        // setup click listeners here
        binding.backIc.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.layoutSaveButton.debouncedClickListener {
            productList?.let { it1 ->
                homeViewModel.updateProduct(it1) }
        }
    }

    private fun openChildFragment(fragment: Fragment,tag : String, addtoBackstack : Boolean) {
        val fragmentObj = parentFragmentManager.beginTransaction().replace(R.id.fragment_container_view, fragment)
        if(addtoBackstack) {
            fragmentObj.addToBackStack(tag)
        }
        fragmentObj.commitAllowingStateLoss()
    }



}