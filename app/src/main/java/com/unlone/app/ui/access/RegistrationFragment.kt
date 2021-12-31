package com.unlone.app.ui.access

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import com.unlone.app.R
import com.unlone.app.databinding.FragmentRegistrationBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*


class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    val mAuth = FirebaseAuth.getInstance()
    private lateinit var functions: FirebaseFunctions


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        functions = Firebase.functions


        val email = binding.emailEv
        val password = binding.passwordEv
        val confirmPassword = binding.confirmPasswordEv
        val register = binding.registerBtn
        val declaration = binding.declaration

        // remove later
        declaration.visibility = View.GONE

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
                Toast.makeText(context, this.getString(R.string.validate_school_email),
                    Toast.LENGTH_SHORT).show()

                validateSchoolEmail(strEmail)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            val e = task.exception
                            if (e is FirebaseFunctionsException) {
                                val code = e.code
                                val details = e.details
                                Log.e("TAG", "\n$code\n$details")
                            }
                            Toast.makeText(context, "error: $e", Toast.LENGTH_SHORT).show()
                            e?.toString()?.let { it1 -> Log.e("TAG", it1) }
                        }else{
                            Log.d("TAG", task.result)
                            if (task.result == "true") {
                                // school email is validated, perform register process
                                CoroutineScope(Dispatchers.Main).launch {
                                    performRegister(strEmail, strPassword, mAuth)
                                }
                            } else {
                                context?.let { it1 ->
                                    MaterialAlertDialogBuilder(it1)
                                        .setTitle(resources.getString(R.string.validation_failed))
                                        .setMessage(resources.getString(R.string.validation_failed_supporting_text))
                                        .setPositiveButton(resources.getString(R.string.continue_text)) { _, _ ->
                                        }
                                        .show()
                                }
                            }
                        }
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

        // Sign in with email
        binding.progressBar.visibility = View.VISIBLE
        try{
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
        } catch(e: FirebaseAuthUserCollisionException){
            Toast.makeText(
                context, e.message,
                Toast.LENGTH_LONG
            ).show()

        }finally {
            // optional finally block
            binding.progressBar.visibility = View.GONE
            binding.registerBtn.isEnabled = true
        }
    }

    private fun validateSchoolEmail(email: String): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "text" to email,
            "push" to true
        )

        return functions
            .getHttpsCallable("validateSchoolEmail")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result: HashMap<String, String> = task.result.data as HashMap<String, String>
                result.values.toList()[0]
            }
    }

}