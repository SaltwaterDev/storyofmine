package com.example.unlone.ui.profile

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unlone.databinding.RecyclerviewProfileCardBinding
import com.example.unlone.utils.dpConvertPx


class ProfileAdapter(var context: Context) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>(){

    var dataList = emptyList<ProfileCard>()

    internal fun setDataList(dataList: List<ProfileCard>){
        this.dataList = dataList
    }

    class ViewHolder(val binding: RecyclerviewProfileCardBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerviewProfileCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title.text = dataList[position].title
        holder.binding.cardView
            .setBackgroundColor(Color.parseColor(dataList[position].backgroundColour))
        holder.binding.cardView.radius = dpConvertPx(5, context).toFloat()
    }


    override fun getItemCount() = dataList.size

}