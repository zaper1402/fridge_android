package com.ashir.fridge.ui.recipe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.R
import com.ashir.fridge.databinding.RecipeItemBinding
import com.ashir.fridge.ui.recipe.pojo.RecipesData
import com.ashir.fridge.utils.listeners.DelegateClickListener
import com.bumptech.glide.Glide
import com.threemusketeers.dliverCustomer.main.utils.extensions.getAppContext
import com.threemusketeers.dliverCustomer.main.utils.extensions.getSafe

class RecipeAdapter(private val recipeList: List<RecipesData>?, private val delegateClickListener: DelegateClickListener?): RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), delegateClickListener)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        recipeList?.getSafe(position)?.let { holder.bindView(it) }
    }

    override fun getItemCount(): Int {
        return recipeList?.size ?: 0
    }

    inner class RecipeViewHolder(private val binding: RecipeItemBinding, private val delegateClickListener: DelegateClickListener?) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(recipeData: RecipesData){
            val recipe = recipeData.recipeInfo
            Glide.with(getAppContext()).load(recipe?.imageUrl).into(binding.recipeIv)
            binding.recipeName.text = recipe?.name
            binding.recipeDesc.text = recipe?.description
            //binding.itemCountTv.text = recipe.name
            binding.timeTv.text = binding.root.context.getString(R.string.x_min, recipe?.ttc.toString())
            if(recipe?.favourite == true){
                binding.favouriteIv.setImageResource(R.drawable.ic_fav)
            }else{
                binding.favouriteIv.setImageResource(R.drawable.ic_unfav)
            }

            binding.root.setOnClickListener {
                delegateClickListener?.onClick(recipe, adapterPosition, null)
            }

            binding.favouriteIv.setOnClickListener {
                delegateClickListener?.onClick(recipe, adapterPosition, null)
            }
        }
    }
}