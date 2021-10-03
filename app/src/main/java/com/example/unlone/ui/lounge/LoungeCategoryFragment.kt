package com.example.unlone.ui.lounge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.unlone.databinding.FragmentLoungeCategoryBinding
import android.widget.ArrayAdapter

import android.util.Log
import androidx.lifecycle.ViewModelProvider


class LoungeCategoryFragment : Fragment() {
    private val binding get() = _binding!!
    private var _binding: FragmentLoungeCategoryBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoungeCategoryBinding.inflate(inflater, container, false)
        val view = binding.root

        val model = ViewModelProvider(this).get(CategoriesViewModel::class.java)
        model.loadCategories()
        model.categories.observe(viewLifecycleOwner, { categories ->
            Log.d("TAG", categories.toString())
            val adapter: ArrayAdapter<*> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                categories
            )
            binding.listview.adapter = adapter

        })


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