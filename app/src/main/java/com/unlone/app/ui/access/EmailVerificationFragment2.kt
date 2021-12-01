package com.unlone.app.ui.access

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.unlone.app.databinding.FragmentEmailVerification2Binding


class EmailVerificationFragment2 : Fragment() {

    private var _binding: FragmentEmailVerification2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVerification2Binding.inflate(inflater, container, false)

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