package com.example.unlone.ui.access

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.unlone.R
import com.example.unlone.databinding.FragmentLoginBinding
import com.example.unlone.databinding.FragmentRegistrationBinding
import com.example.unlone.instance.User
import com.example.unlone.ui.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    val mAuth = FirebaseAuth.getInstance()
    val mFirestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)



        val username = binding.username
        val email = binding.email
        val password = binding.password
        val confirmPassword = binding.confirmPassword
        val register = binding.registerBtn
        val progressBar = binding.progressBar
        val declaration = binding.decalration

        register.setOnClickListener {
            if (username.text.toString().isBlank()) {
                Toast.makeText(
                    context, "Please type your username",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (email.text.toString().isBlank()) {
                Toast.makeText(
                    context, "Please type your email",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.text.toString().isBlank()) {
                Toast.makeText(
                    context, "Please type your password",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.text.toString().length < 6) {
                Toast.makeText(
                    context,
                    "Password must have at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.text != confirmPassword.text){
                Toast.makeText(
                    context,
                    "Password is not the same as Confirm Password",
                    Toast.LENGTH_SHORT
                ).show()
            }else if(!declaration.isChecked){
                Toast.makeText(
                    context,
                    "You must agree the term and condition",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                val strEmail = email.text.toString()
                val strPassword = password.text.toString()
                performRegister(strEmail, strPassword, mAuth)
            }
        }

        return binding.root

}


    private fun performRegister(email: String, password: String, mAuth: FirebaseAuth) {
        //TODO: this is signInAnonymously
        /*mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("REGISTRATION", "start perform registration");
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("REGISTRATION", "signInAnonymously:success");
                                progressBar.setVisibility(View.VISIBLE);
                                FirebaseUser user = mAuth.getCurrentUser();
                                saveUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("REGISTRATION", "signInAnonymously:failure", task.getException());
                                Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/

        // Sign in with email
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        binding.progressBar.visibility = View.VISIBLE
                        val user: FirebaseUser? = mAuth.currentUser
                        if (user != null) {
                            saveUser(user)
                        }
                    }
                }
    }

    private fun saveUser(firebaseUser: FirebaseUser) {
        val uid: String = firebaseUser.uid
        Log.d("REGISTRATION", "save user uid: $uid")
        val username = binding.username.text.toString()
        Log.d("REGISTRATION", "save user username: $username")
        val user = User(uid, username)
        mFirestore.collection("users").document(uid).set(user)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    Log.d("REGISTRATION", "user saved")
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(
                        context, "Account Created",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    (task.exception)?.message?.let {
                        Log.d(
                            "REGISTRATION", it
                        )
                    }
                    Toast.makeText(
                        context,
                        task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.INVISIBLE
                }
            })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegistrationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            RegistrationFragment().apply {

            }
    }
}