package com.unlone.app.ui.lounge.common

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.instance.ListAdapterItem

abstract class BaseAdapter<BINDING : ViewDataBinding, T : ListAdapterItem>(
    var data: List<T>
) : ListAdapter<T, BaseAdapter.BaseViewHolder<BINDING>>(DiffCallback()) {

    @get:LayoutRes
    abstract val layoutId: Int

    open class BaseViewHolder<BINDING : ViewDataBinding>(private val binder: BINDING) :
        RecyclerView.ViewHolder(binder.root) {
        open fun <T> bind(item: T) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BINDING> {
        val binder = DataBindingUtil.inflate<BINDING>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )

        return BaseViewHolder(binder)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BINDING>, position: Int) {
        val item = getItem(position)
        Log.d("TAG", "item loaded: $item")
        holder.bind(item)
    }

    open class DiffCallback<T>: DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            TODO("Not yet implemented")
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            TODO("Not yet implemented")
        }

    }

}