package com.ashir.fridge.ui.recipe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashir.fridge.R
import com.ashir.fridge.databinding.FragmentRecipeDetailsBinding
import com.ashir.fridge.databinding.FragmentRecipesBinding
import com.ashir.fridge.ui.recipe.adapters.ListAdapter
import com.ashir.fridge.ui.recipe.adapters.RecipeAdapter
import com.ashir.fridge.ui.recipe.pojo.RecipesData
import com.bumptech.glide.Glide
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.getAppContext

class RecipeDetailsFragment: Fragment() {

    private var mBinding: FragmentRecipeDetailsBinding? = null
    private val binding get() = mBinding!!
    private var recipeData: RecipesData? = null

    companion object{
        const val TAG = "RecipeDetailsFragment"
        const val RECIPE_DATA = "recipe_data"
        fun newInstance(recipesData: RecipesData?) = RecipeDetailsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(RECIPE_DATA, recipesData)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipeData = arguments?.getSerializable(RECIPE_DATA) as? RecipesData
        setUpUi()
        setClickListeners()
    }

    private fun setUpUi(){
        val recipeInfo = recipeData?.recipeInfo
        mBinding?.recipeIv?.let { Glide.with(getAppContext()).load(recipeInfo?.imageUrl).into(it) }
        mBinding?.recipeTv?.text = recipeInfo?.name
        mBinding?.timeTv?.text = getString(R.string.x_min, recipeInfo?.ttc?.toString())
        mBinding?.recipeDesc?.text = recipeInfo?.description

        mBinding?.ingredientsRv?.layoutManager = LinearLayoutManager(activity)
        val ingredientsList = mutableListOf<String>()
        for (ingredient in recipeInfo?.ingredients!!){
            ingredient.name?.let { ingredientsList.add(it) }
        }
        val ingredientsAdapter = ListAdapter(ingredientsList)
        mBinding?.ingredientsRv?.adapter = ingredientsAdapter

        mBinding?.instructionRv?.layoutManager = LinearLayoutManager(activity)
        val instructionsAdapter = recipeInfo.instructions?.let { ListAdapter(it) }
        mBinding?.instructionRv?.adapter = instructionsAdapter

        mBinding?.servingValueTv?.text = recipeInfo.servings?.toString()
        mBinding?.difficultyValueTv?.text = recipeInfo.difficulty
    }

    private fun setClickListeners(){
        binding.backIc.debouncedClickListener{

        }

        binding.favIc.debouncedClickListener{

        }

        binding.shareIc.debouncedClickListener{

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}