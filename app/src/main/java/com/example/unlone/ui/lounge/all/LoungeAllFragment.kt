package com.example.unlone.ui.lounge.all

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.unlone.databinding.FragmentLoungeAllBinding
import com.example.unlone.instance.Post
import com.example.unlone.ui.create.PostActivity
import com.example.unlone.ui.lounge.common.PostsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LoungeAllFragment : Fragment() {
    private var viewModel: LoungeAllViewModel? = null
    private var postsAdapter: PostsAdapter? = null
    private val mPosts = 100
    private var isLoading = false

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var _binding: FragmentLoungeAllBinding? = null


    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentLoungeAllBinding.inflate(inflater, container, false)
        val view = binding.root


        // create "writing post" button
        val fab: FloatingActionButton = binding.fab
        fab.tooltipText = "Write a post"
        fab.setOnClickListener {
            val intent = Intent(context, PostActivity::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = binding.recycleviewPosts
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        postsAdapter = PostsAdapter(requireActivity())
        recyclerView.adapter = postsAdapter
        viewModel = ViewModelProvider(this).get(LoungeAllViewModel::class.java)
        viewModel!!.loadPosts(mPosts, false)
        viewModel!!.posts.observe(
            viewLifecycleOwner,
            { postList: List<Post> -> postsAdapter!!.setPostList(postList) })

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            viewModel!!.loadPosts(mPosts, false)
            viewModel!!.posts.observe(viewLifecycleOwner, { postList: List<Post> ->
                Log.d("TAG", postList.toString())
                postsAdapter!!.setPostList(postList)
            })
            swipeRefreshLayout.isRefreshing = false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //super.onScrolled(recyclerView, dx, dy);
                /*
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

                 */
            }
        })


        // init search bar
        binding.inputSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                viewModel!!.searchPost(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {}
        })

        // TODO ("add post sorting")

        return view
    }


    companion object {
        const val REQUEST_CODE_ADD_POST = 1
    }
}