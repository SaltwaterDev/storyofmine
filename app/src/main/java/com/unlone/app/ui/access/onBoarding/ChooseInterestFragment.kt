package com.unlone.app.ui.access.onBoarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.unlone.app.R
import com.unlone.app.databinding.FragmentChooseInterestBinding


class ChooseInterestFragment : Fragment() {
    private var _binding: FragmentChooseInterestBinding? = null
    private val binding get() = _binding!!
    private val userSetupViewModel: UserSetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseInterestBinding.inflate(inflater, container, false)
        val view = binding.root
        for (c in userSetupViewModel.interests.value!!) {
            addChip(c, binding.chipGroup)
        }

        binding.nextBtn.setOnClickListener {
            // save user
            saveUserInterests()
            Log.d("TAG", "user saved successfully")
            binding.nextBtn.isEnabled = false
            if (findNavController().currentDestination?.id == R.id.chooseInterestFragment)
                findNavController().navigate(R.id.action_chooseInterestFragment_to_chooseCategoriesFragment)
        }

        binding.backBtn.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.chooseInterestFragment)
                findNavController().navigate(R.id.action_chooseInterestFragment_to_chooseIdentityFragment)
        }
        return view
    }

    private fun addChip(c: String, chipGroup: ChipGroup) {
        val chip = Chip(context)
        chip.text = c
        chip.isCheckable = true
        chipGroup.addView(chip)
    }

    private fun saveUserInterests() {
        // fetch interests
        val selectedInterests = ArrayList<String>()
        for (chip in binding.chipGroup) {
            if ((chip as Chip).isChecked) {
                selectedInterests.add(chip.text.toString())
            }
        }
        userSetupViewModel.setInterests(selectedInterests)
    }

}