package com.unlone.app.ui.profile

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unlone.app.R
import com.unlone.app.databinding.FragmentSavedPostsBinding
import com.unlone.app.instance.Post
import com.unlone.app.ui.lounge.common.PostsAdapter


class SavedPostsFragment : Fragment() {
    private var savePostsViewModel: SavedPostsViewModel? = null
    private var postsAdapter: PostsAdapter? = null
    private val mPosts = 10
    private var isLoading = false

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var _binding: FragmentSavedPostsBinding? = null


    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentSavedPostsBinding.inflate(inflater, container, false)
        val view = binding.root

        // set return button on toolbar
        binding.topAppBar.setNavigationOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_savedStoriesFragment_to_navigation_profile)
        }

        val recyclerView: RecyclerView = binding.recyclerviewPosts
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        postsAdapter = PostsAdapter(requireActivity())
        recyclerView.adapter = postsAdapter
        savePostsViewModel = ViewModelProvider(this).get(SavedPostsViewModel::class.java)
        savePostsViewModel!!.posts.observe(
            viewLifecycleOwner,
            { postList: List<Post> -> postsAdapter!!.setPostList(postList) })
        savePostsViewModel!!.loadPosts()


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
                        savePostsViewModel!!.loadPosts()
                        savePostsViewModel!!.posts.observe(
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