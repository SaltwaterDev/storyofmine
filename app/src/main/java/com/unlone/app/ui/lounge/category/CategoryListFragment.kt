package com.unlone.app.ui.lounge.category

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.unlone.app.databinding.FragmentCategoryListBinding
import com.unlone.app.viewmodel.CategoryListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryListFragment : Fragment() {
    private val binding get() = _binding!!
    private var _binding: FragmentCategoryListBinding? = null
    val model: CategoryListViewModel by lazy { ViewModelProvider(this)[CategoryListViewModel::class.java] }
    private val folAdapter = FollowingCateListAdapter { openSpecificTopic(it) }
    private val allAdapter = AllCateListAdapter { openSpecificTopic(it) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.followingTopicListview.adapter = folAdapter
        binding.allTopicListview.adapter = allAdapter


        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        model.followingCategories.observe(viewLifecycleOwner) { followingCategories ->
            Log.d("TAG", followingCategories.toString())
            folAdapter.submitList(followingCategories)
        }
        model.categories.observe(viewLifecycleOwner) { categories ->
            Log.d("TAG", categories.toString())
            allAdapter.submitList(categories)
        }
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