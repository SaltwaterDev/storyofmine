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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.unlone.app.databinding.FragmentChooseCategoriesBinding


class ChooseCategoriesFragment : Fragment() {

    private var _binding: FragmentChooseCategoriesBinding? = null
    private val binding get() = _binding!!
    private val userSetupViewModel: UserSetupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChooseCategoriesBinding.inflate(inflater, container, false)
        val view = binding.root

        // add categories to chip group
        for (c in userSetupViewModel.categories.value!!) {
            addChip(c, binding.chipGroup)
        }

        binding.nextBtn.setOnClickListener {
            // save user
            saveUserCategories()
            Log.d("TAG", "user saved successfully")
            binding.nextBtn.isEnabled = false
            // TODO ("navigate to next fragment")
        }
        binding.backBtn.setOnClickListener {
            val fragmentManager = (activity as FragmentActivity).supportFragmentManager
            fragmentManager.popBackStack()
        }

        return view
    }

    private fun addChip(c: String, chipGroup: ChipGroup) {
        val chip = Chip(context)
        chip.text = c
        chip.isCheckable = true
        chipGroup.addView(chip)
    }

    private fun saveUserCategories() {
        // read categories
        val selectedCategories = ArrayList<String>()
        for (chip in binding.chipGroup) {
            if ((chip as Chip).isChecked) {
                selectedCategories.add(chip.text.toString())
            }
        }
        userSetupViewModel.setCategories(selectedCategories)
    }

}