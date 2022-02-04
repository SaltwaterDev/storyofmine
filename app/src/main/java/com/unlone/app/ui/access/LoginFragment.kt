package com.unlone.app.ui.access

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.unlone.app.R
import com.unlone.app.databinding.FragmentLoginBinding
import com.unlone.app.ui.MainActivity
import com.unlone.app.viewmodel.LoginViewModel
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initObservers()

        binding.buttonRegister.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_registrationFragment)
        }


        viewModel.userExistedInFireStore.observe(viewLifecycleOwner) {
            if (it == true) {
                val intent = Intent(context, MainActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            } else if (it == false) {
                // user data is not written to fireStore yet
                findNavController().navigate(R.id.action_loginFragment_to_on_boarding_navigation)
            }
        }

        // This callback will only be called when Fragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            activity?.finish()
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