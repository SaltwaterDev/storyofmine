package com.example.unlone.ui.profile

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unlone.databinding.FragmentProfileBinding
import com.example.unlone.instance.User
import com.example.unlone.ui.PostsAdapter
import com.example.unlone.ui.profile.EditProfileActivity
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.toObject

class ProfileFragment : Fragment() {
    private var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var mFirestore: FirebaseFirestore? = FirebaseFirestore.getInstance()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var profileList = mutableListOf<ProfileCard>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView: RecyclerView = binding.recyclerviewProfile
        val profileAdapter = ProfileAdapter(requireActivity())
        val layoutManager = GridLayoutManager(activity, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = profileAdapter

        profileList.add(ProfileCard("My \nStories", "#DCCDCB"))
        profileList.add(ProfileCard("Journey", "#B9CAB7"))
        profileList.add(ProfileCard("Saved", "#F2EAE5"))
        profileList.add(ProfileCard("Setting", "#E2E0EE"))
        profileAdapter.setDataList(profileList)

        val docRef = mFirestore!!.collection("users").document(
            currentUser!!.uid
        )
        docRef.addSnapshotListener(EventListener { value, error ->
            if (error != null) {
                System.err.println("Listen failed: $error")
                return@EventListener
            }
            if (value != null && value.exists()) {
                println("Current data: " + value.data)
                val user = value.toObject<User>()
                if (user != null) {
                    binding.name.text = user.username
                    binding.bio.text = user.bio
                }
            } else {
                print("Current data: null")
            }
        })

        binding.editButton.setOnClickListener{
            startActivity(
                Intent(
                    context,
                    EditProfileActivity::class.java
                )
            )
        }

        return view
    }
}