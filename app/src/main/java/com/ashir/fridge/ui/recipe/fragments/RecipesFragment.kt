package com.ashir.fridge.ui.recipe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ashir.fridge.R
import com.ashir.fridge.account.pojo.CuisineType
import com.ashir.fridge.account.pojo.Cuisines
import com.ashir.fridge.databinding.FragmentRecipesBinding
import com.ashir.fridge.http.Result
import com.ashir.fridge.ui.home.HomeViewModel
import com.ashir.fridge.ui.recipe.RecipeViewModel
import com.ashir.fridge.ui.recipe.adapters.RecipesViewPagerAdapter
import com.ashir.fridge.ui.recipe.pojo.Recipes
import com.ashir.fridge.utils.IModel
import com.ashir.fridge.utils.listeners.DelegateClickListener
import com.google.android.material.tabs.TabLayoutMediator
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.isNull
import com.threemusketeers.dliverCustomer.main.utils.extensions.setGone
import com.threemusketeers.dliverCustomer.main.utils.extensions.setGoneMultiple
import com.threemusketeers.dliverCustomer.main.utils.extensions.setVisible
import com.threemusketeers.dliverCustomer.main.utils.extensions.setVisibleMultiple

class RecipesFragment: Fragment() {

    private var mBinding: FragmentRecipesBinding? = null
    private val binding get() = mBinding!!
    private val recipeViewModel: RecipeViewModel by viewModels<RecipeViewModel>()
    private var mData:Recipes? = null
    private var cuisineId: String? = null

    companion object{
        const val TAG = "RecipesFragment"
        const val CUISINE_ID = "cuisineId"

        fun newInstance(cuisineId: String?) = RecipesFragment().apply {
            arguments = Bundle().apply {
                putString(CUISINE_ID, cuisineId)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cuisineId = arguments?.getString(CUISINE_ID)
        setUpObservers()
        setClickListeners()
        recipeViewModel.getRecipes(cuisineId)
    }

    private val recipeClickListener = object : DelegateClickListener {
        override fun onClick(iModel: IModel?, position: Int, otherData: Any?) {
            when (iModel) {
                is CuisineType -> {
                    val cuisineName = iModel.cuisineName
                    Toast.makeText(context, cuisineName, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUpUi(){
        val adapter = activity?.let { RecipesViewPagerAdapter(it, mData, recipeClickListener) }
        mBinding?.viewPager?.adapter = adapter

        mBinding?.tabLayout?.let { tabLayout ->
            mBinding?.viewPager?.let { viewPager ->
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = when (position) {
                        0 -> getString(R.string.breakfast)
                        1 -> getString(R.string.lunch)
                        2 -> getString(R.string.dinner)
                        else -> getString(R.string.breakfast)
                    }
                }.attach()
            }
        }
        showSuccessView()
    }

    private fun setUpObservers(){
        recipeViewModel.recipesLiveData.observe(viewLifecycleOwner){
            when(it){
                is Result.Success -> {
                    if(it.data.isNull() ){
                        setupEmptyState()
                    }else {
                        mData = it.data
                        setUpUi()
                    }
                }
                is Result.Error<*> -> {
                    setErrorState()
                }
                is Result.InProgress -> {
                    setLoadingState()
                }
            }
        }
    }

    private fun setupEmptyState() {
        // Setup Empty State
        binding.tabLayout.setGoneMultiple(binding.viewPager)
        binding.emptyString.text = getString(R.string.the_fridge_is_empty_now_nplease_add_ingredients)
        binding.emptyString.setVisible()
    }

    private fun setLoadingState() {
        // Set Loading State
        binding.tabLayout.setGoneMultiple(binding.viewPager)
        binding.emptyString.text = getString(R.string.please_wait_fetching_all_your_items)
        binding.emptyString.setVisible()
    }

    private fun setErrorState() {
        // Set Error State
        binding.tabLayout.setGoneMultiple(binding.viewPager)
        binding.emptyString.text = getString(R.string.uh_oh_something_went_wrong_please_try_again_later)
        binding.emptyString.setVisible()
    }

    private fun showSuccessView(){
        binding.tabLayout.setVisibleMultiple(binding.viewPager)
        binding.emptyString.setGone()
    }

    private fun setClickListeners(){
        mBinding?.backIc?.debouncedClickListener{

        }

        mBinding?.homeIc?.debouncedClickListener{

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}