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

abstract class BaseAdapter<BINDING : ViewDataBinding, T : ListAdapterItem>
    : ListAdapter<T, BaseAdapter.BaseViewHolder<BINDING>>(DiffCallback()) {

    @get:LayoutRes
    abstract val layoutId: Int

    abstract fun bind(binding: BINDING, item: T)

    open class BaseViewHolder<BINDING : ViewDataBinding>(val binder: BINDING) :
        RecyclerView.ViewHolder(binder.root)

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
        bind(holder.binder, item)
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