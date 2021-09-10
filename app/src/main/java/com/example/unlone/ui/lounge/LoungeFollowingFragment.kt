package com.example.unlone.ui.lounge

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.unlone.ui.PostsAdapter
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.unlone.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.unlone.ui.create.PostActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelProvider
import com.example.unlone.instance.Post

class LoungeFollowingFragment : Fragment() {
    private var homeViewModel: HomeViewModel? = null
    private var postsAdapter: PostsAdapter? = null
    private val mPosts = 10
    private var isLoading = false
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =
            inflater.inflate(R.layout.fragment_lounge_following, container, false) as ViewGroup
        val fab: FloatingActionButton = root.findViewById(R.id.fab)
        fab.tooltipText = "Write a post"
        fab.setOnClickListener { v: View? ->
            startActivityForResult(
                Intent(context, PostActivity::class.java), REQUEST_CODE_ADD_POST
            )
        }
        val mAuth =
            FirebaseAuth.getInstance() // TODO ("will be used when choosing the following topic")

        val recyclerView: RecyclerView = root.findViewById(R.id.recycleview_posts)
        val swipeRefreshLayout: SwipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        postsAdapter = PostsAdapter(requireActivity())
        recyclerView.adapter = postsAdapter
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel!!.loadPosts(mPosts, false)
        homeViewModel!!.posts.observe(
            viewLifecycleOwner,
            { postList: List<Post> -> postsAdapter!!.setPostList(postList) })

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            homeViewModel!!.loadPosts(mPosts, false)
            homeViewModel!!.posts.observe(viewLifecycleOwner, { postList: List<Post> ->
                Log.d("TAG", postList.toString())
                postsAdapter!!.setPostList(postList)
            })
            swipeRefreshLayout.isRefreshing = false
        }

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
                        homeViewModel!!.loadPosts(mPosts, true)
                        homeViewModel!!.posts.observe(
                            viewLifecycleOwner,
                            { postList: List<Post> -> postsAdapter!!.setPostList(postList) })
                        isLoading = false
                    }
                }
            }
        })
        return root
    }

    companion object {
        const val REQUEST_CODE_ADD_POST = 1
    }
}