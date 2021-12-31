package com.unlone.app.ui.access


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.unlone.app.databinding.FragmentEmailVerificationBinding
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unlone.app.R
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*


class EmailVerificationFragment : Fragment() {
    private var _binding: FragmentEmailVerificationBinding? = null
    private val binding get() = _binding!!

    val mFirestore = Firebase.firestore
    val mAuth = Firebase.auth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.sendEmailBtn.setOnClickListener{
            binding.sendEmailBtn.isEnabled = false
            val user = mAuth.currentUser
            CoroutineScope(Dispatchers.Main).launch {
                if (user != null) {
                    val url = "https://unlone.page.link/verify?uid=" + user.uid
                    val actionCodeSettings = ActionCodeSettings.newBuilder()
                        .setUrl(url)
                        .setHandleCodeInApp(false)
                        // The default for this is populated with the current android package name.
                        .setAndroidPackageName("com.unlone.app", false, null)
                        .build()
                    mAuth.setLanguageCode(Locale.getDefault().country)
                    withContext(Dispatchers.IO){
                        user.sendEmailVerification(actionCodeSettings).await()
                        Log.d("TAG", "send verification email successful")
                    }
                }
            }
            Toast.makeText(
                context,
                "Sending...",
                Toast.LENGTH_SHORT
            ).show()
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_emailVerificationFragment_to_emailVerification2)
        }

        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmailVerificationFragment().apply {
            }
    }
}