package com.ashir.fridge.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.R
import com.ashir.fridge.databinding.CategoryProductsItemBinding
import com.ashir.fridge.ui.home.pojo.UserProduct
import com.ashir.fridge.utils.IModel
import com.ashir.fridge.utils.listeners.DelegateClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener
import java.io.Serializable

class CategoryProductsAdapter(private val delegateClickListener: DelegateClickListener) : RecyclerView.Adapter<CategoryProductsAdapter.ViewHolder>() {

    private var products: List<ProductGroupedData> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CategoryProductsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, delegateClickListener)
    }

    override fun onBindViewHolder(holder: CategoryProductsAdapter.ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun submitList(categories: List<ProductGroupedData>) {
        this.products = categories
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: CategoryProductsItemBinding, private val delegateClickListener: DelegateClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductGroupedData) {
            binding.productName.text = product.productName
            binding.productQuantity.text =
                binding.root.context.getString(R.string.quantity, product.totalQuantity.toString(), "Quantity may be sum of multiple products")
            binding.root.debouncedClickListener {
                delegateClickListener.onClick(product, adapterPosition, null)
            }
        }
    }

    data class ProductGroupedData(
        val productName : String,
        val totalQuantity: Int,
        val userProducts: List<UserProduct>
    ): IModel, Serializable
}