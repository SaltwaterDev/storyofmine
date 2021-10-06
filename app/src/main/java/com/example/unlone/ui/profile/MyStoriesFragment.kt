package com.example.unlone.ui.profile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.unlone.R
import com.example.unlone.databinding.FragmentLoungeFollowingBinding
import com.example.unlone.databinding.FragmentMyStoriesBinding
import com.example.unlone.instance.Post
import com.example.unlone.ui.create.PostActivity
import com.example.unlone.ui.lounge.common.PostsAdapter
import com.example.unlone.ui.lounge.following.LoungeFollowingViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth


class MyStoriesFragment : Fragment() {
    private var viewModel: MyStoriesViewModel? = null
    private var postsAdapter: PostsAdapter? = null
    private val mPosts = 10
    private var isLoading = false

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var _binding: FragmentMyStoriesBinding? = null


    @RequiresApi(api = Build.VERSION_CODES.O)
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
        postsAdapter = PostsAdapter(requireActivity())
        recyclerView.adapter = postsAdapter
        viewModel = ViewModelProvider(this).get(MyStoriesViewModel::class.java)
        viewModel!!.loadPosts(mPosts, false)
        viewModel!!.posts.observe(
            viewLifecycleOwner,
            { postList: List<Post> -> postsAdapter!!.setPostList(postList) })



        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        })


        return view
    }


    companion object {
        const val REQUEST_CODE_ADD_POST = 1
    }
}