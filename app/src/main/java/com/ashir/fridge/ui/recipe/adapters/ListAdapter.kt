package com.ashir.fridge.ui.recipe.adapters

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.R
import com.ashir.fridge.databinding.ListItemBinding
import com.threemusketeers.dliverCustomer.main.utils.extensions.getSafe

class ListAdapter(val list: List<String>): RecyclerView.Adapter<ListAdapter.ListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindView(list.getSafe(position))
    }

    class ListViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindView(text: String?){
            if (text.isNullOrEmpty()) return
            colorNumbersInString(binding.listTv, text, R.color.primary)
        }

        private fun colorNumbersInString(textView: TextView, text: String, colorResId: Int) {
            val spannableString = SpannableString(text)
            val color = ContextCompat.getColor(textView.context, colorResId)

            val regex = Regex("\\d+") // Matches one or more digits
            val matches = regex.findAll(text)

            for (match in matches) {
                spannableString.setSpan(
                    ForegroundColorSpan(color),
                    match.range.first,
                    match.range.last + 1,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            textView.text = spannableString
        }
    }
}