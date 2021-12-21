package com.unlone.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.unlone.app.R
import com.unlone.app.databinding.FragmentEditProfileBinding
import com.unlone.app.model.User
import java.util.*

class EditProfileFragment : Fragment() {
    var currentUser: FirebaseUser? = null
    private var storageReference: StorageReference? = null
    private var mFirestore: FirebaseFirestore? = null

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val cancel = binding.cancelButton
        val save = binding.saveButton
        val username = binding.username
        val bio = binding.bio
        // val likes = binding.likes
        // val dislikes = binding.dislikes

        cancel.setOnClickListener { findNavController().navigate(R.id.action_editProfileFragment_to_navigation_profile) }
        save.setOnClickListener {
            save.isEnabled = false
            cancel.isEnabled = false
            updateProfile(username.text.toString(), bio.text.toString())
            findNavController().navigate(R.id.action_editProfileFragment_to_navigation_profile)
            save.isEnabled = true
            cancel.isEnabled = true
        }

        currentUser = FirebaseAuth.getInstance().currentUser
        storageReference = FirebaseStorage.getInstance().getReference("uploads")
        mFirestore = FirebaseFirestore.getInstance()
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
                username.setText(user!!.username)
                bio.setText(user.bio)
                // TODO ("set bio, likes, and dislikes into persona")

            } else {
                print("Current data: null")
            }
        })
    return view
    }

    private fun updateProfile(username: String, bio: String) {
        val docRef = mFirestore!!.collection("users").document(
            currentUser!!.uid
        )
        docRef.get().addOnSuccessListener { //TODO: so far only accept one persona
            mFirestore!!.collection("users").document(currentUser!!.uid)
                .update(
                    "username", username,
                    "bio", bio
                    // TODO (set likes and dislikes)
                )
        }
    }
}