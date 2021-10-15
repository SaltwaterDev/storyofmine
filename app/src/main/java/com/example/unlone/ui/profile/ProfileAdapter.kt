package com.example.unlone.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.unlone.R
import com.example.unlone.databinding.RecyclerviewProfileCardBinding
import com.example.unlone.ui.MainActivity
import com.example.unlone.ui.access.FirstAccessActivity
import com.example.unlone.utils.dpConvertPx
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth


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
            .setCardBackgroundColor(Color.parseColor(dataList[position].backgroundColour))
        holder.binding.cardView.setOnClickListener{ view ->
            when (holder.binding.title.text){
                // Go to "My Stories"
                "My \nStories" -> view.findNavController().navigate(R.id.action_navigation_profile_to_myStoriesFragment)
                // Go to "Saved"
                "Saved" -> view.findNavController().navigate(R.id.action_navigation_profile_to_savedStoriesFragment)
                // Logout
                "Contact us" -> true
                // Logout
                "Log Out" -> logout()
            }
        }

        if(holder.binding.title.text == "Log Out"){
            holder.binding.title.setTextColor(Color.parseColor("#E30B0B"))
        }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Alert")
            .setMessage("Are you sure to logout?")
            .setNegativeButton("Cancel") { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton("Log Out") { dialog, which ->
                val user = FirebaseAuth.getInstance()
                user.signOut()
                val intent = Intent(context, FirstAccessActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()

            }
            .show()
    }


    override fun getItemCount() = dataList.size

}