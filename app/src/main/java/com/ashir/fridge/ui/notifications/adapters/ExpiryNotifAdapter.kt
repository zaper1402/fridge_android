package com.ashir.fridge.ui.notifications.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.databinding.ExpiryNotifItemBinding
import com.ashir.fridge.ui.notifications.pojo.NotifItem
import com.ashir.fridge.utils.listeners.DelegateClickListener
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ExpiryNotifAdapter(private val delegateClickListener: DelegateClickListener) : RecyclerView.Adapter<ExpiryNotifAdapter.ViewHolder>() {
    private var notifs: List<NotifItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ExpiryNotifItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val days = if(position ==0) -1L else daysBetween(notifs[position - 1].expiryDate,notifs[position].expiryDate)
        holder.bind(notifs[position], (position ==0 || ((days in 1..2) && (notifs[position].expiryDate != notifs[position - 1].expiryDate) )))
    }

    override fun getItemCount(): Int = notifs.size

    fun submitList(categories: List<NotifItem>) {
        this.notifs = categories
        notifyDataSetChanged()
    }
    fun daysBetween(startDate: String, endDate: String): Long {
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)
        val days = ChronoUnit.DAYS.between(start, end)
        return days
    }
    fun getHeaderText(days : Long): String {
        return when {
            days <0 -> "Expired"
            days == 0L -> "Today"
            days == 1L -> "Tomorrow"
            else -> "Expiring soon"
        }
    }

    inner class ViewHolder(private val binding: ExpiryNotifItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notif: NotifItem, showHeader: Boolean = false) {
            binding.title.text = notif.message
            if(showHeader) {
                binding.header.visibility = ViewGroup.VISIBLE
                binding.header.text = getHeaderText(daysBetween(LocalDate.now().toString(), notif.expiryDate))
            } else {
                binding.header.visibility = ViewGroup.GONE
            }
        }
    }
}