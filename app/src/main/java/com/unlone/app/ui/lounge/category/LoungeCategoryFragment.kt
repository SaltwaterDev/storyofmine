package com.unlone.app.ui.lounge.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unlone.app.databinding.FragmentLoungeCategoryBinding



class LoungeCategoryFragment : Fragment() {
    private val binding get() = _binding!!
    private var _binding: FragmentLoungeCategoryBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoungeCategoryBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    companion object {
        fun newInstance(): LoungeCategoryFragment {
            val fragment = LoungeCategoryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}