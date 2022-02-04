package com.unlone.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.FragmentMyStoriesBinding
import com.unlone.app.ui.lounge.LoungePostsBaseFragment
import com.unlone.app.viewmodel.MyStoriesViewModel


class MyStoriesFragment :
    LoungePostsBaseFragment<FragmentMyStoriesBinding, MyStoriesViewModel>(R.layout.fragment_my_stories) {
    override var mPosts = 10

    override fun onStart() {
        super.onStart()
        viewModel?.loadPosts(mPosts, false)
        viewModel?.postListUiItems?.observe(
            viewLifecycleOwner
        ) { postList ->
            postListAdapter.submitList(postList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        // set return button on toolbar
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun getViewModelClass(): Class<MyStoriesViewModel> = MyStoriesViewModel::class.java

    override fun initFab(){}

    override fun initRv() {
        val recyclerView: RecyclerView = binding.recyclerviewPosts
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = postListAdapter
    }

    override fun initSwipeRefreshLayout() {}


    companion object {
        const val REQUEST_CODE_ADD_POST = 1
    }
}