package com.example.unlone.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unlone.R
import com.example.unlone.databinding.ActivityEditProfileBinding
import com.example.unlone.instance.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rengwuxian.materialedittext.MaterialEditText
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    var username: MaterialEditText? = null
    var bio: MaterialEditText? = null
    var currentUser: FirebaseUser? = null
    private var storageReference: StorageReference? = null
    private var mFirestore: FirebaseFirestore? = null

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val cancel = binding.cancelButton
        val save = binding.saveButton
        val username = binding.username
        val bio = binding.bio
        val likes = binding.likes
        val dislikes = binding.dislikes

        cancel.setOnClickListener{ finish() }
        save.setOnClickListener{
            updateProfile(username.text.toString(), bio.text.toString())
            finish()
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