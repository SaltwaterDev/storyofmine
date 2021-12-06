package com.unlone.app.ui.profile

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.FragmentMyStoriesBinding
import com.unlone.app.instance.Post
import com.unlone.app.ui.lounge.common.PostsAdapter


class MyStoriesFragment : Fragment() {
    private var viewModel: MyStoriesViewModel? = null
    private var postsAdapter: PostsAdapter? = null
    private val mPosts = 10
    private var isLoading = false

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var _binding: FragmentMyStoriesBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentMyStoriesBinding.inflate(inflater, container, false)
        val view = binding.root

        // set return button on toolbar
        binding.topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_myStoriesFragment_to_navigation_profile)
        }

        val recyclerView: RecyclerView = binding.recyclerviewPosts
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        postsAdapter = PostsAdapter()
        recyclerView.adapter = postsAdapter
        viewModel = ViewModelProvider(this).get(MyStoriesViewModel::class.java)
        viewModel!!.loadPosts(mPosts, false)
        viewModel!!.posts.observe(
            viewLifecycleOwner,
            { postList: List<Post> ->
                postsAdapter!!.submitList(postList) })



        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            /*
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //super.onScrolled(recyclerView, dx, dy);
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val totalItem = linearLayoutManager!!.itemCount
                val lastVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (totalItem < lastVisible + 3) {
                    if (!isLoading) {
                        isLoading = true
                        // load more posts
                        viewModel!!.loadPosts(mPosts, true)
                        viewModel!!.posts.observe(
                            viewLifecycleOwner,
                            { postList: List<Post> -> postsAdapter!!.setPostList(postList) })
                        isLoading = false
                    }
                }
            }

             */
        })


        return view
    }


    companion object {
        const val REQUEST_CODE_ADD_POST = 1
    }
}