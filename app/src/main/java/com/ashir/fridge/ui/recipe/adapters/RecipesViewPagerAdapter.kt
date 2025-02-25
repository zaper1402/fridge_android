package com.ashir.fridge.ui.recipe.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ashir.fridge.ui.recipe.fragments.RecipesGridFragment
import com.ashir.fridge.ui.recipe.pojo.Recipes
import com.ashir.fridge.ui.recipe.pojo.RecipesDataList
import com.ashir.fridge.utils.listeners.DelegateClickListener

class RecipesViewPagerAdapter(val fragmentActivity: FragmentActivity, val recipes: Recipes?, val delegateClickListener: DelegateClickListener) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3 // Number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecipesGridFragment.newInstance(RecipesDataList(recipes?.breakfastRecipes) , delegateClickListener)
            1 -> RecipesGridFragment.newInstance(RecipesDataList(recipes?.lunchRecipes) , delegateClickListener)
            2 -> RecipesGridFragment.newInstance(RecipesDataList(recipes?.dinnerRecipes) , delegateClickListener)
            else -> RecipesGridFragment.newInstance(RecipesDataList(recipes?.breakfastRecipes) , delegateClickListener) // Default case
        }
    }
}