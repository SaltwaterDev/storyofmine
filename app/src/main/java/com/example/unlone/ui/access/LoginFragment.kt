package com.example.unlone.ui.access

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.unlone.R
import com.example.unlone.databinding.FragmentConfigBinding
import com.example.unlone.databinding.FragmentLoginBinding
import com.example.unlone.ui.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
            progressBar.visibility = View.VISIBLE
            val strEmail = email.text.toString()
            val strPassword = password.text.toString()
            if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val reference = mFireStore.collection("users")
                                .document(mAuth.currentUser!!.uid)
                            reference.get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    activity?.startActivity(intent)
                                    activity?.finish()
                                } else {
                                    progressBar.visibility = View.INVISIBLE
                                    Toast.makeText(
                                        context,
                                        "Unknown error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            progressBar.visibility = View.INVISIBLE
                            Toast.makeText(
                                context,
                                "Authentication failed!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
            }
    }
}