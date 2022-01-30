package com.unlone.app.ui.lounge.category

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.unlone.app.databinding.FragmentCategoryListBinding

class CategoryListFragment : Fragment() {
    private val binding get() = _binding!!
    private var _binding: FragmentCategoryListBinding? = null
    val model: CategoriesViewModel by lazy { ViewModelProvider(this)[CategoriesViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        val view = binding.root

        val folAdapter = FollowingCateListAdapter { openSpecificTopic(it) }
        binding.followingTopicListview.adapter = folAdapter
        model.followingCategories.observe(viewLifecycleOwner) { followingCategories ->
            Log.d("TAG", followingCategories.toString())
            folAdapter.submitList(followingCategories)
        }


        val allAdapter = AllCateListAdapter { openSpecificTopic(it) }
        binding.allTopicListview.adapter = allAdapter
        model.categories.observe(viewLifecycleOwner) { categories ->
            Log.d("TAG", categories.toString())
            allAdapter.submitList(categories)
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }


    // navigate to specific topic
    private fun openSpecificTopic(topic: String) {
        Log.d(TAG, "selected topic: $topic")
        val selectedTopic = if (topic.first() != '#') model.retrieveDefaultCategory(topic).toString() else topic
        if (selectedTopic != "null"){
            // open the post with specific category
            val action = CategoryListFragmentDirections.navigateToCategoryPostFragment(selectedTopic)
            view?.let { Navigation.findNavController(it).navigate(action) }
        }
        else{
            Log.d(TAG, "couldn't find the category")
        }
    }
}