package com.unlone.app.ui.lounge.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.unlone.app.databinding.FragmentCategoryListBinding
import com.unlone.app.viewmodel.CategoryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

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
            Timber.d(followingCategories.toString())
            folAdapter.submitList(followingCategories)
        }
        model.categories.observe(viewLifecycleOwner) { categories ->
            Timber.d(categories.toString())
            allAdapter.submitList(categories)
        }
    }


    // navigate to specific topic
    private fun openSpecificTopic(topic: String) {
        Timber.d("selected topic: $topic")
        if (topic.first() != '#')
            lifecycleScope.launch {
                model.retrieveDefaultCategory(topic).collect { selectedTopic ->
                    if (selectedTopic != "null") {
                        // open the post with specific category
                        val action =
                            CategoryListFragmentDirections.navigateToCategoryPostFragment(
                                selectedTopic
                            )
                        view?.let { Navigation.findNavController(it).navigate(action) }
                    } else {
                        Timber.d("couldn't find the category")
                    }
                }
            }
        else {
            if (topic != "null") {
                // open the post with specific category
                val action =
                    CategoryListFragmentDirections.navigateToCategoryPostFragment(topic)
                view?.let { Navigation.findNavController(it).navigate(action) }
            } else {
                Timber.d("couldn't find the category")
            }
        }
    }
}