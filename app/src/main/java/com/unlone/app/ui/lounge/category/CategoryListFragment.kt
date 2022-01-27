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
import androidx.navigation.fragment.findNavController
import com.unlone.app.databinding.FragmentCategoryListBinding

class CategoryListFragment : Fragment() {
    private val binding get() = _binding!!
    private var _binding: FragmentCategoryListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        val view = binding.root

        val model = ViewModelProvider(this)[CategoriesViewModel::class.java]
        model.followingCategories.observe(viewLifecycleOwner, { followingCategories ->
            Log.d("TAG", followingCategories.toString())
            val adapter: ArrayAdapter<*> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                followingCategories
            )
            binding.followingTopicListview.adapter = adapter
        })

        model.categories.observe(viewLifecycleOwner, { categories ->
            Log.d("TAG", categories.toString())
            val adapter: ArrayAdapter<*> = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                categories
            )
            binding.allTopicListview.adapter = adapter
        })

        binding.followingTopicListview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                var selectedTopic = parent.getItemAtPosition(position) as String
                Log.d(TAG, "selected category: $selectedTopic")
                if (selectedTopic.first() != '#')
                    selectedTopic = model.retrieveDefaultCategory(selectedTopic).toString()
                if (selectedTopic != "null"){
                    // open the post with specific category
                    val action = CategoryListFragmentDirections.navigateToCategoryPostFragment(selectedTopic)
                    Navigation.findNavController(view).navigate(action)
                }
                else{
                    Log.d(TAG, "couldn't find the category")
                }
            }

        binding.allTopicListview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                var selectedTopic = parent.getItemAtPosition(position) as String
                Log.d(TAG, "selected topic: $selectedTopic")
                selectedTopic = model.retrieveDefaultCategory(selectedTopic).toString()
                if (selectedTopic != "null"){
                    // open the post with specific category
                    val action = CategoryListFragmentDirections.navigateToCategoryPostFragment(selectedTopic)
                    Navigation.findNavController(view).navigate(action)
                }
                else{
                    Log.d(TAG, "couldn't find the category")
                }
            }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}