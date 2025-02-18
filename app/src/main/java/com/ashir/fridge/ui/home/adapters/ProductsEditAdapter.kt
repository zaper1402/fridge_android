package com.ashir.fridge.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.databinding.ProductEditRvItemBinding
import com.ashir.fridge.ui.home.pojo.UserProduct
import com.ashir.fridge.utils.listeners.DelegateClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.debouncedClickListener
import com.threemusketeers.dliverCustomer.main.utils.extensions.isNull

class ProductsEditAdapter(private val delegateClickListener: DelegateClickListener) : RecyclerView.Adapter<ProductsEditAdapter.ViewHolder>() {

    private var products: List<UserProduct> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductEditRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, delegateClickListener)
    }

    override fun onBindViewHolder(holder: ProductsEditAdapter.ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun submitList(categories: List<UserProduct>) {
        this.products = categories
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ProductEditRvItemBinding, private val delegateClickListener: DelegateClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: UserProduct) {
            if(product.currentQuantity.isNull()){
                product.currentQuantity = product.quantity
            }
            binding.name.text = product.name
            binding.expiry.text = "Expiry: ${product.expiryDate}"
            binding.quantity.text = "${product.currentQuantity?.toInt()}"
            binding.icPlus.debouncedClickListener {
                product.currentQuantity = product.currentQuantity?.plus(1)
                binding.quantity.text = "${product.currentQuantity?.toInt()}"
            }
            binding.icMinus.debouncedClickListener {
                if(product.currentQuantity!! > 0){
                    product.currentQuantity = product.currentQuantity?.minus(1)
                    binding.quantity.text = "${product.currentQuantity?.toInt()}"
                }
            }
            binding.root.debouncedClickListener{
                delegateClickListener.onClick(product, adapterPosition, null)
            }
        }
    }
}