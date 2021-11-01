package com.example.unlone.ui.access

import android.content.ContentValues.TAG
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
import androidx.navigation.Navigation
import com.example.unlone.R
import com.example.unlone.databinding.FragmentLoginBinding
import com.example.unlone.databinding.FragmentRegistrationBinding
import com.example.unlone.instance.User
import com.example.unlone.ui.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)


        val email = binding.emailEv
        val password = binding.passwordEv
        val confirmPassword = binding.confirmPasswordEv
        val register = binding.registerBtn
        val declaration = binding.declaration

        register.setOnClickListener {
            if (email.text.toString().isBlank()) {
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
            } else if (password.text.toString() != confirmPassword.text.toString()){
                Toast.makeText(
                    context,
                    "Password is not the same as Confirm Password",
                    Toast.LENGTH_SHORT
                ).show()
            }
            /*else if(!declaration.isChecked){
                Toast.makeText(
                    context,
                    "You must agree the term and condition",
                    Toast.LENGTH_SHORT
                ).show()
            }*/
            else {
                val strEmail = email.text.toString()
                val strPassword = password.text.toString()
                CoroutineScope(Dispatchers.Main).launch {
                    performRegister(strEmail, strPassword, mAuth)
                }
            }
        }

        binding.emailField.setEndIconOnClickListener {
            // Respond to help icon presses
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setMessage(resources.getString(R.string.school_email_description))
                    .show()
            }
        }

        return binding.root

}


    private suspend fun performRegister(email: String, password: String, mAuth: FirebaseAuth) {
        binding.registerBtn.isEnabled = false
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
        binding.progressBar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password).await()
        val user: FirebaseUser? = mAuth.currentUser
        if (user != null) {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_registrationFragment_to_emailVerificationFragment)
        }else{
            Toast.makeText(
                context, "register failed for some reason",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.progressBar.visibility = View.GONE
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