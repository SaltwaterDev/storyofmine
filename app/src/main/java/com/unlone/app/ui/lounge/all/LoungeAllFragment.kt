package com.unlone.app.ui.lounge.all

import android.os.Build
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import com.unlone.app.ui.create.PostActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.unlone.app.R
import com.unlone.app.databinding.FragmentLoungeAllBinding
import com.unlone.app.instance.Post
import com.unlone.app.ui.lounge.common.LoungePostsBaseFragment

class LoungeAllFragment :
    LoungePostsBaseFragment<FragmentLoungeAllBinding, LoungeAllViewModel>(R.layout.fragment_lounge_all) {

    override fun onStart() {
        super.onStart()
        viewModel?.loadPosts(mPosts, false)
        viewModel?.posts?.observe(
            viewLifecycleOwner, { postList: List<Post> ->
                postsAdapter.submitList(postList)
            })
    }

    override fun getViewModelClass() = LoungeAllViewModel::class.java

    override fun initFab() {
        val fab: FloatingActionButton = binding.fab
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fab.tooltipText = resources.getString(R.string.write_a_post)
        }
        fab.setOnClickListener {
            val intent = Intent(context, PostActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initRv() {
        val recyclerView: RecyclerView = binding.recycleviewPosts
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = postsAdapter
    }

    override fun initSearchBar() {
        binding.inputSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                viewModel?.searchPost(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
    }

    override fun initSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            viewModel?.loadPosts(mPosts, false)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_POST = 1
    }
}