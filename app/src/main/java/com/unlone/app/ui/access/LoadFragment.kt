package com.unlone.app.ui.access

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.unlone.app.R
import com.unlone.app.databinding.FragmentLoadBinding
import com.unlone.app.databinding.FragmentLoginBinding
import com.unlone.app.model.User
import com.unlone.app.ui.MainActivity

class LoadFragment : Fragment() {
    private var mAuth: FirebaseAuth = Firebase.auth
    private var mFirestore: FirebaseFirestore = Firebase.firestore
    private var _binding: FragmentLoadBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            Log.d("LOADACTIVITY", "first time login")
            findNavController().navigate(R.id.action_loadFragment_to_first_access_navigation)
        } else {
            Log.d("LOADACTIVITY", "isEmailVerified = "+currentUser.isEmailVerified.toString())
            Log.d("LOADACTIVITY", "uid: ${currentUser.uid}")
            if(!currentUser.isEmailVerified){
                Toast.makeText(
                    context, "You have not verify the email",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_loadFragment_to_first_access_navigation)
            }else{
                mFirestore.collection("users").document(currentUser.uid).get()
                    .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                        Log.d("LOADACTIVITY", "GET USER")
                        if (documentSnapshot.data != null){
                            val current = documentSnapshot.toObject<User>()
                            if (current != null) {
                                Toast.makeText(
                                    context, "Welcome Back " + current.username,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Log.d("LOADACTIVITY", "login: " + currentUser.uid)
                            startActivity(Intent(activity, MainActivity::class.java))
                            activity?.finish()
                        } else{
                            Log.d("LOADACTIVITY", "user have not set up")
                            findNavController().navigate(R.id.action_loadFragment_to_first_access_navigation)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d("LOADACTIVITY", "exception: \n$e")
                        findNavController().navigate(R.id.action_loadFragment_to_first_access_navigation)
                    }
            }
        }
    }
}