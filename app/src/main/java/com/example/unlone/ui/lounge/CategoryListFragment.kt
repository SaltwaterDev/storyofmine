package com.example.unlone.ui.lounge

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.unlone.databinding.FragmentCategoryListBinding

class CategoryListFragment : Fragment() {
    private val binding get() = _binding!!
    private var _binding: FragmentCategoryListBinding? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
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

        binding.listview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedCategory = parent.getItemAtPosition(position) as String
                Log.d(TAG, "selected category: $selectedCategory")
                // open the post with specific category
                val action = CategoryListFragmentDirections.navigateToCategoryPostFragment(selectedCategory)
                Navigation.findNavController(view).navigate(action)
            }

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