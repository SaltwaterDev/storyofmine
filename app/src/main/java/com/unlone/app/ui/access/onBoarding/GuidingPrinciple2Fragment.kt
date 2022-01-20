package com.unlone.app.ui.access.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.unlone.app.R
import com.unlone.app.databinding.FragmentGuidingPrinciple1Binding
import com.unlone.app.databinding.FragmentGuidingPrinciple2Binding

class GuidingPrinciple2Fragment : Fragment() {
    private var _binding: FragmentGuidingPrinciple2Binding? = null
    private val binding get() = _binding!!
    private val userSetupViewModel: UserSetupViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuidingPrinciple2Binding.inflate(inflater, container, false)
        val view = binding.root

        binding.finishBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.guidingPrinciple2Fragment)
                findNavController().navigate(R.id.action_guidingPrinciple2Fragment_to_MainActivityDestination)
        }
        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.guidingPrinciple2Fragment)
                findNavController().navigate(
                    R.id.action_guidingPrinciple2Fragment_to_guidingPrinciple1Fragment
                )
        }


        return view
    }
}