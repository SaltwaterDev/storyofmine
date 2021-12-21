package com.unlone.app.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.ListItemProfileCardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.unlone.app.model.ProfileCard
import com.unlone.app.ui.access.StartupActivity


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
                // Go to "My Stories"
                holder.itemView.context.getString(R.string.my_stories) -> view.findNavController().navigate(R.id.action_navigation_profile_to_myStoriesFragment)
                // Go to "Saved"
                holder.itemView.context.getString(R.string.saved) -> view.findNavController().navigate(R.id.action_navigation_profile_to_savedStoriesFragment)
                // Logout
                holder.itemView.context.getString(R.string.contact) -> view.findNavController().navigate(R.id.action_navigation_profile_to_contactUsFragment)
                // Logout
                holder.itemView.context.getString(R.string.logout) -> logout(holder)
            }
        }

        if(holder.binding.title.text == holder.itemView.context.getString(R.string.logout)){
            holder.binding.title.setTextColor(Color.parseColor("#C10909"))
        }
    }

    // TODO (move it to viewModel)
    private fun logout(holder: ViewHolder) {
        MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(holder.itemView.context.getString(R.string.delete_alert))
            .setMessage(holder.itemView.context.getString(R.string.logout_alert))
            .setPositiveButton(holder.itemView.context.getString(R.string.logout)) { _, _ ->
                val user = FirebaseAuth.getInstance()
                user.signOut()
                val intent = Intent(context, StartupActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            }
            .show()
    }


    override fun getItemCount() = dataList.size

}