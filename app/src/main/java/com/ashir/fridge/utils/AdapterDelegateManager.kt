package com.threemusketeers.myapplication.main.utils

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.ashir.fridge.utils.IModel
import com.ashir.fridge.utils.listeners.DelegateClickListener

class AdapterDelegateManager<T : AdapterDelegate<V, RecyclerView.ViewHolder>?, V : Any> :
    DelegateClickListener {
    private val TAG = "AdapterDelegateManager"

    protected var delegates: SparseArrayCompat<T> = SparseArrayCompat()

    private var externalDelegateClickListener: DelegateClickListener? = null


    fun addDelegate(viewType: Int, delegate: T) {
        delegate!!.setOnClickListener(this)
        delegates.put(viewType, delegate)
    }

    private fun getDelegateForData(data: V): T? {
        for (i in 0 until delegates.size()) {
            val key = delegates.keyAt(i)
            val obj = delegates[key]!!
            if (obj.isItemForViewType(data)) {
                val viewType = obj.getViewType(data)
                return delegates[viewType]
            }
        }
        return null
    }

    fun setDelegateClickListener(externalDelegateClickListener: DelegateClickListener?) {
        this.externalDelegateClickListener = externalDelegateClickListener
    }

    fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val delegate: AdapterDelegate<*, *> = delegates[viewType]!!

        return delegate.onCreateViewHolder(parent, viewType)!!
    }

    fun onBindViewHolder(item: V, position: Int, viewHolder: RecyclerView.ViewHolder) {
        (getDelegateForData(item) ?: return).onBindViewHolder(item, viewHolder, position)
    }

    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder?, item: V) {
       holder?.let {
            (getDelegateForData(item) ?: return).onViewAttachedToWindow(holder, item)
        }
    }

    fun getItemViewType(data: V): Int {
        for (i in 0 until delegates.size()) {
            val key = delegates.keyAt(i)
            val obj = delegates[key]!!
            if (obj.isItemForViewType(data)) {
                return obj.getViewType(data)
            }
        }
        return -1
    }

    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder?, item: V) {
        holder?.let {
            (getDelegateForData(item) ?: return@onViewDetachedFromWindow).onViewDetachedFromWindow(it, item)
        }
    }

    fun onDestroy() {
        for (i in 0 until delegates.size()) {
            delegates.get(i)?.onDestroy()
        }
    }

    /*
    * All Delegate clicks r handled by this.
    * */
    override fun onClick(iModel: IModel?, position: Int, otherData: Any?) {
        externalDelegateClickListener?.onClick(iModel, position, otherData)
    }
}

