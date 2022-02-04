package com.unlone.app.ui.access

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.unlone.app.R
import com.unlone.app.databinding.FragmentRegistrationBinding
import com.unlone.app.viewmodel.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
@InternalCoroutinesApi
class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegistrationViewModel by lazy {
        ViewModelProvider(this)[RegistrationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initObservers()

        binding.emailField.setEndIconOnClickListener {
            // Respond to help icon presses
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setMessage(resources.getString(R.string.school_email_description))
                    .show()
            }
        }

        viewModel.navToVerification.observe(viewLifecycleOwner) {
            if (it == true)
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_registrationFragment_to_emailVerificationFragment)
            else Toast.makeText(
                context, "register failed for some reason",
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.showValidationErrorMsg.observe(viewLifecycleOwner) {
            if (it) {
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

        return binding.root

    }

    private fun initObservers() {
        viewModel.message.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                // Toast the [message]
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}