package com.unlone.app.ui.access

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.unlone.app.R
import com.unlone.app.databinding.FragmentLoginBinding
import com.unlone.app.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val mAuth = FirebaseAuth.getInstance()
        val mFireStore = FirebaseFirestore.getInstance()

        val email = binding.email
        val password = binding.password
        val login = binding.buttonLogin
        val register = binding.buttonRegister
        val progressBar = binding.progressBar

        register.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        login.setOnClickListener {
            login.isEnabled = false
            register.isEnabled = false

            progressBar.visibility = View.VISIBLE
            val strEmail = email.text.toString()
            val strPassword = password.text.toString()
            if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val reference = mFireStore.collection("users")
                                .document(mAuth.currentUser!!.uid)
                            viewLifecycleOwner.lifecycleScope.launch {
                                try {
                                    val result = reference.get().await()
                                    Log.d("TAG", "result = $result")
                                    if (result.data == null){
                                        // user data is not written to fireStore yet
                                        progressBar.visibility = View.INVISIBLE
                                        findNavController().navigate(R.id.action_loginFragment_to_on_boarding_navigation)
                                    } else{
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        activity?.startActivity(intent)
                                        activity?.finish()
                                    }
                                } catch (e: FirebaseFirestoreException) {
                                    Log.d("TAG", "FirebaseFirestoreException: $e")
                                }
                            }
                        } else {
                            progressBar.visibility = View.INVISIBLE
                            Toast.makeText(
                                context,
                                "Authentication failed!",
                                Toast.LENGTH_SHORT
                            ).show()
                            login.isEnabled = true
                            register.isEnabled = true
                        }
                    }
            }
        }
        return binding.root
    }
}