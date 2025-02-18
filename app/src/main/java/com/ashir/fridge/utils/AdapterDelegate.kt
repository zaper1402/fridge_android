package com.threemusketeers.myapplication.main.utils

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.utils.listeners.DelegateClickListener


abstract class AdapterDelegate<T, V : RecyclerView.ViewHolder?> {
    protected var mDelegateClickListener: DelegateClickListener? = null

    abstract fun isItemForViewType(data: T): Boolean

    abstract fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): V

    abstract fun onBindViewHolder(data: T, holder: V, position: Int)

    abstract fun getViewType(data: T): Int

    fun onViewAttachedToWindow(holder: V, data: T) {
    }

    fun onViewDetachedFromWindow(holder: V, data: T) {
    }

    fun onDestroy() {
    }

    fun setOnClickListener(onClickListener: DelegateClickListener?) {
        mDelegateClickListener = onClickListener
    }
}
