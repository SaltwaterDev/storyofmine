package com.unlone.app.ui.access.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.unlone.app.R
import com.unlone.app.databinding.FragmentGuidingPrinciple4Binding
import com.unlone.app.viewmodel.UserSetupViewModel

class GuidingPrinciple4Fragment : Fragment() {
    private var _binding: FragmentGuidingPrinciple4Binding? = null
    private val binding get() = _binding!!
    private val userSetupViewModel: UserSetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuidingPrinciple4Binding.inflate(inflater, container, false)
        val view = binding.root

        binding.finishBtn.setOnClickListener {
                findNavController().navigate(R.id.action_guidingPrinciple4Fragment_to_MainActivityDestination)
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }


        return view
    }
}