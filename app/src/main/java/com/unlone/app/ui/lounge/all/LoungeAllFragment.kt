package com.unlone.app.ui.lounge.all

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.unlone.app.R
import com.unlone.app.databinding.FragmentLoungeAllBinding
import com.unlone.app.instance.Post
import com.unlone.app.ui.create.PostActivity
import com.unlone.app.ui.lounge.common.PostsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LoungeAllFragment : Fragment() {
    private var allViewModel: LoungeAllViewModel? = null
    private var postsAdapter: PostsAdapter? = null
    private val mPosts = 100
    private var isLoading = false

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var _binding: FragmentLoungeAllBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentLoungeAllBinding.inflate(inflater, container, false)
        val view = binding.root

        // create "writing post" button
        val fab: FloatingActionButton = binding.fab
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fab.tooltipText = resources.getString(R.string.write_a_post)
        }
        fab.setOnClickListener {
            val intent = Intent(context, PostActivity::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = binding.recycleviewPosts
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        postsAdapter = PostsAdapter()
        recyclerView.adapter = postsAdapter
        allViewModel = ViewModelProvider(this).get(LoungeAllViewModel::class.java)
        allViewModel!!.loadPosts(mPosts, false)
        allViewModel!!.getPosts().observe(
            viewLifecycleOwner,
            { postList ->
                postsAdapter!!.submitList(postList)
            })

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            allViewModel!!.loadPosts(mPosts, false)
            swipeRefreshLayout.isRefreshing = false
        }




        // init search bar
        binding.inputSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                allViewModel!!.searchPost(s.toString())
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