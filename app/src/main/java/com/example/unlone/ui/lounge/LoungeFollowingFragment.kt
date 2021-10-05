package com.example.unlone.ui.lounge

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.unlone.ui.create.PostActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModelProvider
import com.example.unlone.databinding.FragmentLoungeFollowingBinding
import com.example.unlone.instance.Post

class LoungeFollowingFragment : Fragment() {
    private var homeViewModel: LoungeFollowingViewModel? = null
    private var postsAdapter: PostsAdapter? = null
    private val mPosts = 10
    private var isLoading = false

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var _binding: FragmentLoungeFollowingBinding? = null


    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentLoungeFollowingBinding.inflate(inflater, container, false)
        val view = binding.root


        // create "writing post" button
        val fab: FloatingActionButton = binding.fab
        fab.tooltipText = "Write a post"
        fab.setOnClickListener {
            val intent = Intent(context, PostActivity::class.java)
            startActivity(intent)
        }

        val mAuth =
            FirebaseAuth.getInstance() // TODO ("will be used when choosing the following topic")
        val recyclerView: RecyclerView = binding.recycleviewPosts
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        postsAdapter = PostsAdapter(requireActivity())
        recyclerView.adapter = postsAdapter
        homeViewModel = ViewModelProvider(this).get(LoungeFollowingViewModel::class.java)
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


        // init search bar
        binding.inputsearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                homeViewModel!!.searchPost(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {}
        })


        return view
    }


    companion object {
        const val REQUEST_CODE_ADD_POST = 1
    }
}