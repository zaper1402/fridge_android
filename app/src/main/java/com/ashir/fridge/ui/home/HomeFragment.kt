package com.ashir.fridge.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ashir.fridge.MainActivity
import com.ashir.fridge.R
import com.ashir.fridge.account.AccountManager
import com.ashir.fridge.account.pojo.Categories
import com.ashir.fridge.databinding.FragmentHomeBinding
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.MainActivityViewModel
import com.ashir.fridge.ui.home.fragments.SearchProductFragment
import com.threemusketeers.dliverCustomer.main.utils.extensions.isNull
import com.threemusketeers.dliverCustomer.main.utils.extensions.setGone
import com.threemusketeers.dliverCustomer.main.utils.extensions.setGoneMultiple
import com.threemusketeers.dliverCustomer.main.utils.extensions.setVisible
import com.threemusketeers.dliverCustomer.main.utils.extensions.setVisibleMultiple
import okhttp3.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels<MainActivityViewModel>()
    private val categoriesRvAdapter = HomeCategoriesRv()
    private var categoriesData : MutableList<Categories>  = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUi() {
        // Setup UI
        binding.emptyString.text = getString(R.string.please_wait_fetching_all_your_items)
        binding.emptyString.setVisible()
        if(AccountManager.user == null) {
            mainActivityViewModel.getSelfUser()
        }else {
            binding.header.text = getString(R.string.s_fridge, AccountManager.user?.name)
            homeViewModel.getInventoryCategories()
        }
    }

    private fun setupObservers() {
        // Setup Observer
        mainActivityViewModel.getSelfUserLiveData.observe(viewLifecycleOwner ){
            when(it){
                is Result.Success -> {
                    AccountManager.saveAccountInfo(it.data)
                    setupUi()
                }
                is Result.Error<*> -> {
                    val resp = it.responseBody as? Response
                    if(resp?.code == 404){
                        (activity as? MainActivity)?.setupPreLogin()
                    }else {
                        // todo
                    }
                }
                is Result.InProgress -> {

                }
            }
        }

        homeViewModel.inventoryCategoriesLiveData.observe(viewLifecycleOwner){
            when(it){
                is Result.Success -> {
//                    it.data is List<Categories>
                    if(it.data.isNull() ){
                        setupEmptyState()
                    }else {
                        categoriesData = mutableListOf()
                        (it.data as? List<Categories>)?.let { it1 -> categoriesData.addAll(it1) }
                        setupCategoriesRv()
                    }
                }
                is Result.Error<*> -> {
                    // todo
                }
                is Result.InProgress -> {

                }
            }
        }
    }

    private fun setupCategoriesRv() {
        // Setup Categories RecyclerView
        binding.categoriesRv.setVisibleMultiple(binding.letsCookBtn)
        binding.categoriesRv.adapter = categoriesRvAdapter
        categoriesRvAdapter.submitList(categoriesData)
        binding.emptyString.setGone()
    }

    private fun setupEmptyState() {
        // Setup Empty State
        binding.categoriesRv.setGoneMultiple(binding.letsCookBtn)
        binding.emptyString.text = getString(R.string.the_fridge_is_empty_now_nplease_add_ingredients)
        binding.emptyString.setVisible()
    }

    private fun setupClickListeners() {
        // Setup Click Listeners
        binding.floatingAddBtn.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, SearchProductFragment.newInstance("")).addToBackStack(SearchProductFragment.TAG)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}