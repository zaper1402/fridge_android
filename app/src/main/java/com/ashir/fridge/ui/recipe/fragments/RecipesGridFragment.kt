package com.ashir.fridge.ui.recipe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ashir.fridge.databinding.RecipesGridFragmentBinding
import com.ashir.fridge.ui.home.adapters.CuisinesAdapter
import com.ashir.fridge.ui.recipe.adapters.RecipeAdapter
import com.ashir.fridge.ui.recipe.pojo.RecipeInfo
import com.ashir.fridge.ui.recipe.pojo.RecipesData
import com.ashir.fridge.ui.recipe.pojo.RecipesDataList
import com.ashir.fridge.utils.listeners.DelegateClickListener

class RecipesGridFragment: Fragment() {

    private var mBinding: RecipesGridFragmentBinding? = null
    private val binding get() = mBinding!!
    private var recipesData: RecipesDataList? = null
    private var recipes: List<RecipesData>? = null
    private var mDelegateClickListener: DelegateClickListener? = null

    companion object{
        const val TAG = "RecipesGridFragment"
        const val RECIPES_DATA = "recipesData"
        fun newInstance(recipesData: RecipesDataList?, delegateClickListener: DelegateClickListener?) = RecipesGridFragment().apply {
            arguments = Bundle().apply {
                putSerializable(RECIPES_DATA, recipesData)
            }
            mDelegateClickListener = delegateClickListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = RecipesGridFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipesData = arguments?.getSerializable(RECIPES_DATA) as? RecipesDataList
        recipes = recipesData?.recipesDataList
        setUpUi()
    }

    private fun setUpUi(){
        val gridLayoutManager = GridLayoutManager(activity, 2)
        mBinding?.recipeRv?.layoutManager = gridLayoutManager
        val adapter = RecipeAdapter(recipes, mDelegateClickListener)
        mBinding?.recipeRv?.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}