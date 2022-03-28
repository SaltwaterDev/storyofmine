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
import com.unlone.app.databinding.FragmentOnBoarding2Binding
import com.unlone.app.viewmodel.UserSetupViewModel

class OnBoarding2Fragment : Fragment() {
    private var _binding: FragmentOnBoarding2Binding? = null
    private val binding get() = _binding!!
    val mAuth = Firebase.auth
    private val userSetupViewModel: UserSetupViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoarding2Binding.inflate(inflater, container, false)
        val view = binding.root

        if (userSetupViewModel.user.username != null) {
            binding.usernameEv.text
        }


        binding.nextBtn.setOnClickListener {
            if (binding.usernameEv.text.toString().isNotEmpty()) {
                // pass the user data to next page
                val username = binding.usernameEv.text.toString()
                // update user profile
                Log.d("TAG", "user username: $username")
                userSetupViewModel.setUserName(username)
                val profileUpdates = userProfileChangeRequest {
                    displayName = username
                }
                if (findNavController().currentDestination?.id == R.id.onBoarding2Fragment)
                    findNavController().navigate(R.id.action_onBoarding2Fragment_to_chooseInterestFragment)
            } else {
                Toast.makeText(
                    context,
                    "Please type your name~",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }


    companion object {
        @JvmStatic
        fun newInstance() {
        }
    }

}