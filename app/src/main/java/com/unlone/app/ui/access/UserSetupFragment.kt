package com.unlone.app.ui.access

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.unlone.app.databinding.FragmentUserSetupBinding
import com.unlone.app.instance.User
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class UserSetupFragment : Fragment() {
    private var _binding: FragmentUserSetupBinding? = null
    private val binding get() = _binding!!

    val mAuth = Firebase.auth

    private lateinit var identity: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserSetupBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.nextBtn.isEnabled = false

        // check if user's email verified
        Log.d("TAG", "isEmailVerified: ${mAuth.currentUser?.isEmailVerified}")

        binding.helpSeeker.setOnClickListener{
            uncheckOtherCards(it as MaterialCardView)
            identity = "helpSeeker"
        }
        binding.expressor.setOnClickListener{
            uncheckOtherCards(it as MaterialCardView)
            identity = "expressor"
        }
        binding.helper.setOnClickListener{
            uncheckOtherCards(it as MaterialCardView)
        }
        binding.reader.setOnClickListener{
            uncheckOtherCards(it as MaterialCardView)
            identity = "reader"
        }


        binding.nextBtn.setOnClickListener{
            if (binding.usernameEv.text.toString().isNotEmpty()){
                val firebaseUser: FirebaseUser? = mAuth.currentUser
                // pass the user data to next page
                val uid: String = firebaseUser?.uid as String
                Log.d("TAG", "user uid: $uid")
                val username = binding.usernameEv.text.toString()
                Log.d("TAG", "user username: $username")
                // update user profile
                val profileUpdates = userProfileChangeRequest {
                    displayName = username
                }
                CoroutineScope(Dispatchers.IO).launch{
                    firebaseUser.updateProfile(profileUpdates).await()
                }

                val user = User(uid, username, identity = identity)
                val action = UserSetupFragmentDirections.toChooseCategoriesFragment(user=user)
                findNavController().navigate(action)
            }
            else{
                Toast.makeText(
                    context,
                    "Please type your name~",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

            return view
    }

    private fun uncheckOtherCards(it: MaterialCardView) {
        binding.nextBtn.isEnabled = true

        binding.helpSeeker.isChecked = false
        binding.expressor.isChecked = false
        binding.helper.isChecked = false
        binding.reader.isChecked = false

        it.isChecked = true
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() {
            }
    }
}