package com.unlone.app.ui.profile

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.FragmentProfileBinding
import com.unlone.app.instance.User
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.toObject
import com.unlone.app.instance.ProfileCard

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

        profileList.add(ProfileCard(getString(R.string.my_stories), "#EDC678"))
        // profileList.add(ProfileCard(getString(R.string.journey), "#E6E2CB"))   TODO
        profileList.add(ProfileCard(getString(R.string.saved), "#E2E0EE"))
        // profileList.add(ProfileCard(getString(R.string.setting), "#B0B0B0"))   TODO
        profileList.add(ProfileCard(getString(R.string.contact), "#B5CEF0"))
        profileList.add(ProfileCard(getString(R.string.logout), "#B9CAB7"))
        profileAdapter.setDataList(profileList.distinct())

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
                    if(!user.bio.isNullOrBlank()){
                        binding.bio.text = user.bio
                        binding.bio.visibility = View.VISIBLE
                    }
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