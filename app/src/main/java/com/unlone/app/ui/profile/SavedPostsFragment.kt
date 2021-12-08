package com.unlone.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.FragmentSavedPostsBinding
import com.unlone.app.instance.Post
import com.unlone.app.ui.lounge.common.LoungePostsBaseFragment

class SavedPostsFragment :
    LoungePostsBaseFragment<FragmentSavedPostsBinding, SavedPostsViewModel>(R.layout.fragment_saved_posts) {
    override var mPosts = 100

    override fun onStart() {
        super.onStart()
        viewModel?.loadPosts(mPosts)
        viewModel?.posts?.observe(
            viewLifecycleOwner, { postList: List<Post> ->
                postsAdapter.submitList(postList)
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        // set return button on toolbar
        binding.topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_savedStoriesFragment_to_navigation_profile)
        }
        return binding.root
    }

    override fun getViewModelClass(): Class<SavedPostsViewModel> = SavedPostsViewModel::class.java

    override fun initFab(){}

    override fun initRv() {
        val recyclerView: RecyclerView = binding.recyclerviewPosts
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = postsAdapter
    }

    override fun initSwipeRefreshLayout() {}

    override fun initSearchBar() {}

    companion object {
        const val REQUEST_CODE_ADD_POST = 1
    }
}