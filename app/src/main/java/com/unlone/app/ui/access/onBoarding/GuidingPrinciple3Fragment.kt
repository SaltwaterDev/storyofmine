package com.unlone.app.ui.access.onBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.unlone.app.R
import com.unlone.app.databinding.FragmentChooseIdentityBinding
import com.unlone.app.databinding.FragmentGuidingPrinciple1Binding
import com.unlone.app.databinding.FragmentGuidingPrinciple3Binding

class GuidingPrinciple3Fragment : Fragment() {
    private var _binding: FragmentGuidingPrinciple3Binding? = null
    private val binding get() = _binding!!
    private val userSetupViewModel: UserSetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuidingPrinciple3Binding.inflate(inflater, container, false)
        val view = binding.root

        binding.nextBtn.setOnClickListener {
            findNavController()
                .navigate(R.id.action_guidingPrinciple3Fragment_to_guidingPrinciple4Fragment)
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }


        return view
    }

}