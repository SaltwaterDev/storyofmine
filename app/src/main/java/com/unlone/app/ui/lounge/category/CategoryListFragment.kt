package com.unlone.app.ui.lounge.category

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.unlone.app.databinding.FragmentCategoryListBinding
import com.unlone.app.ui.lounge.LoungeCategoryFragment

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
                var selectedCategory = parent.getItemAtPosition(position) as String
                Log.d(TAG, "selected category: $selectedCategory")
                selectedCategory = model.retrieveDefaultCategory(selectedCategory).toString()
                if (selectedCategory != "null"){
                    // open the post with specific category
                    val action = CategoryListFragmentDirections.navigateToCategoryPostFragment(selectedCategory)
                    Navigation.findNavController(view).navigate(action)
                }
                else{
                    Log.d(TAG, "couldn't find the category")
                }
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