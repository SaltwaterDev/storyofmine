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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.FragmentProfileBinding
import com.unlone.app.model.User
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.toObject
import com.unlone.app.model.ProfileCard

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var profileList = mutableListOf<ProfileCard>()
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this)[ProfileViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.loadUserProfile()
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

        binding.editButton.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        return view
    }
}