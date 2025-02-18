package com.ashir.fridge.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.account.pojo.Categories
import com.ashir.fridge.databinding.CategoriesGridItemBinding

class HomeCategoriesRv : RecyclerView.Adapter<HomeCategoriesRv.ViewHolder>() {

    private var categories: List<Categories> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CategoriesGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun submitList(categories: List<Categories>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    class ViewHolder(binding: CategoriesGridItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Categories) {

        }
    }
}