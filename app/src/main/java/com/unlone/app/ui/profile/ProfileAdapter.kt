package com.unlone.app.ui.profile

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.ListItemProfileCardBinding
import com.unlone.app.model.ProfileCard
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class ProfileAdapter(var context: Context) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>(){

    var dataList = emptyList<ProfileCard>()

    internal fun setDataList(dataList: List<ProfileCard>){
        this.dataList = dataList
    }

    class ViewHolder(val binding: ListItemProfileCardBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemProfileCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title.text = dataList[position].title
        holder.binding.cardView
            .setCardBackgroundColor(Color.parseColor(dataList[position].backgroundColour))
        holder.binding.cardView.setOnClickListener{ view ->
            when (holder.binding.title.text){
                holder.itemView.context.getString(R.string.my_stories) -> view.findNavController().navigate(R.id.action_navigation_profile_to_myStoriesFragment)
                holder.itemView.context.getString(R.string.saved) -> view.findNavController().navigate(R.id.action_navigation_profile_to_savedStoriesFragment)
                holder.itemView.context.getString(R.string.contact) -> view.findNavController().navigate(R.id.action_navigation_profile_to_contactUsFragment)
                holder.itemView.context.getString(R.string.setting) -> view.findNavController().navigate(R.id.action_navigation_profile_to_settingFragment)
            }
        }

        if(holder.binding.title.text == holder.itemView.context.getString(R.string.logout)){
            holder.binding.title.setTextColor(Color.parseColor("#C10909"))
        }
    }


    override fun getItemCount() = dataList.size

}