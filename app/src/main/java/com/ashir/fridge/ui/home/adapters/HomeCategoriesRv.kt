package com.ashir.fridge.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.account.pojo.HomeProductCategory
import com.ashir.fridge.databinding.CategoriesGridItemBinding
import com.ashir.fridge.utils.listeners.DelegateClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener

class HomeCategoriesRv(private val delegateClickListener: DelegateClickListener) : RecyclerView.Adapter<HomeCategoriesRv.ViewHolder>() {

    private var categories: List<HomeProductCategory> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CategoriesGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, delegateClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun submitList(categories: List<HomeProductCategory>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: CategoriesGridItemBinding, private val delegateClickListener: DelegateClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: HomeProductCategory) {
            binding.categoryName.text = category.productCategory
            binding.root.debouncedClickListener {
                delegateClickListener.onClick(category, adapterPosition, null)
            }
        }
    }
}