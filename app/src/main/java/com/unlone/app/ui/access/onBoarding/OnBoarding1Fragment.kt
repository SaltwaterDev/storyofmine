package com.unlone.app.ui.access.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.unlone.app.R
import com.unlone.app.databinding.FragmentOnBoarding1Binding


class OnBoarding1Fragment : Fragment() {
    private var _binding: FragmentOnBoarding1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoarding1Binding.inflate(inflater, container, false)
        val view = binding.root

        binding.startBtn.setOnClickListener {
            findNavController().navigate(R.id.action_onBoarding1Fragment_to_onBoarding2Fragment)
        }
        return view
    }
}