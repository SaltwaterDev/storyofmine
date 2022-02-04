package com.unlone.app.ui.access.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.unlone.app.R
import com.unlone.app.databinding.FragmentChooseIdentityBinding
import com.unlone.app.viewmodel.UserSetupViewModel


class ChooseIdentityFragment : Fragment() {
    private var _binding: FragmentChooseIdentityBinding? = null
    private val binding get() = _binding!!
    private val userSetupViewModel: UserSetupViewModel by activityViewModels()
    private lateinit var identity: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseIdentityBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.nextBtn.isEnabled = false


        binding.helpSeeker.setOnClickListener {
            uncheckOtherCards(it as MaterialCardView)
            identity = "helpSeeker"
        }
        binding.expressor.setOnClickListener {
            uncheckOtherCards(it as MaterialCardView)
            identity = "expressor"
        }
        binding.helper.setOnClickListener {
            uncheckOtherCards(it as MaterialCardView)
            identity = "helper"
        }
        binding.reader.setOnClickListener {
            uncheckOtherCards(it as MaterialCardView)
            identity = "reader"
        }

        binding.nextBtn.setOnClickListener {
            userSetupViewModel.setIdentity(identity)
            if (findNavController().currentDestination?.id == R.id.chooseIdentityFragment)
                findNavController().navigate(R.id.chooseIdentityFragment_to_chooseInterestFragment)
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
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
    }