package com.unlone.app.ui.access

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.unlone.app.R
import com.unlone.app.databinding.FragmentEmailVerification2Binding


class EmailVerificationFragment2 : Fragment() {

    private var _binding: FragmentEmailVerification2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVerification2Binding.inflate(inflater, container, false)

        // This callback will only be called when Fragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            context?.let { context ->
                MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
                    .setTitle(context.getString(R.string.reminding))
                    .setMessage(context.getString(R.string.email_verification_redirect))
                    .setPositiveButton(context.getString(R.string.proceed)) { _, _ ->
                        val user = FirebaseAuth.getInstance()
                        user.signOut()
                        findNavController().navigate(R.id.action_emailVerificationFragment2_to_loginFragment)
                    }
                    .show()
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmailVerificationFragment2().apply {
                arguments = Bundle().apply {
                }
            }
    }
}