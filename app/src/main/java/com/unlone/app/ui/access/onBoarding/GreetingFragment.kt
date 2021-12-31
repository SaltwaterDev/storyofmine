package com.unlone.app.ui.access.onBoarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.unlone.app.R
import com.unlone.app.databinding.FragmentGreetingBinding

class GreetingFragment : Fragment() {
    private var _binding: FragmentGreetingBinding? = null
    private val binding get() = _binding!!
    val mAuth = Firebase.auth
    private val userSetupViewModel: UserSetupViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGreetingBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.startBtn.setOnClickListener {
            if (binding.usernameEv.text.toString().isNotEmpty()) {
                // pass the user data to next page
                val username = binding.usernameEv.text.toString()
                // update user profile
                Log.d("TAG", "user username: $username")
                userSetupViewModel.setUserName(username)
                val profileUpdates = userProfileChangeRequest {
                    displayName = username
                }
                findNavController().navigate(R.id.action_greetingFragment_to_chooseIdentityFragment)
            } else {
                Toast.makeText(
                    context,
                    "Please type your name~",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() {
        }
    }

}