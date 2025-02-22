package com.ashir.fridge.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.R
import com.ashir.fridge.account.pojo.CuisineType
import com.ashir.fridge.databinding.CuisineItemBinding
import com.ashir.fridge.ui.home.HomeUtils
import com.ashir.fridge.utils.listeners.DelegateClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener

class CuisinesAdapter(private val cuisineList: List<CuisineType>?, private val delegateClickListener: DelegateClickListener): RecyclerView.Adapter<CuisinesAdapter.CuisinesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisinesViewHolder {
        val binding = CuisineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CuisinesViewHolder(binding, delegateClickListener)
    }

    override fun onBindViewHolder(holder: CuisinesViewHolder, position: Int) {
        holder.bindView(cuisineList?.get(position))
    }

    override fun getItemCount(): Int {
        return cuisineList?.size ?: 0
    }


    class CuisinesViewHolder(private val  binding: CuisineItemBinding, private val delegateClickListener: DelegateClickListener): RecyclerView.ViewHolder(binding.root){
        fun bindView(cuisine: CuisineType?){
            val cuisineName = cuisine?.cuisineName
            binding.cuisineTv.text = cuisineName
            val cuisineRes = HomeUtils.cuisineToImageMap[cuisineName]
            if (cuisineRes != null) {
                binding.cuisineIv.setImageResource(cuisineRes)
            }else{
                //fallback
                binding.cuisineIv.setImageResource(R.drawable.indian)
            }

            binding.root.debouncedClickListener {
                delegateClickListener.onClick(cuisine, adapterPosition, null)
            }
        }
    }
}