package com.unlone.app.ui.lounge.category

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.unlone.app.R
import com.unlone.app.databinding.FragmentCategoryPostBinding
import com.unlone.app.ui.lounge.LoungePostsBaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class CategoryPostFragment :
    LoungePostsBaseFragment<FragmentCategoryPostBinding, CategoriesViewModel>(R.layout.fragment_category_post) {
    private val args: CategoryPostFragmentArgs by navArgs()
    private val category by lazy {
        args.category
    }

    private var isFollowing by Delegates.notNull<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        super.onCreateView(inflater, container, savedInstanceState)
        // set category title
        setCategoryTitle()
        val followingButton = binding.followBtn
        isFollowing(followingButton)
        initFollowingButton()

        // load posts
        val recyclerView: RecyclerView = binding.recycleviewPosts
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = postListAdapter
        viewModel?.loadPosts(category)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }




        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // load posts
        viewModel!!.postListUiItems.observe(
            viewLifecycleOwner
        ) { postList ->
            postListAdapter.submitList(postList)
        }
    }

    private fun initFollowingButton() {
        binding.followBtn.setOnClickListener {
            if (isFollowing) {
                // if follow is true, unfollow it
                viewModel!!.followCategory(category, true)
                binding.followBtn.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_to_follow_btn)
                )
                isFollowing = false

            } else {
                // if follow is false, follow it
                viewModel!!.followCategory(category, false)
                binding.followBtn.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_following_btn)
                )
                isFollowing = true
            }
        }
    }

    private fun isFollowing(followingBtn: ImageView) {
        lifecycleScope.launch(Dispatchers.IO) {
            isFollowing = viewModel?.isFollowing(category) == true
            withContext(Dispatchers.Main) {
                if (isFollowing) {
                    followingBtn.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_following_btn)
                    )
                } else {
                    followingBtn.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_to_follow_btn)
                    )
                }
            }
        }
    }

    private fun setCategoryTitle() {
        if (category.first() != '#') {
            viewModel?.getCategoryTitle(args.category)
            viewModel?.categoryTitle?.observe(viewLifecycleOwner) { title ->
                binding.topicTv.text = title
            }
        } else
            binding.topicTv.text = category
    }

    override fun getViewModelClass(): Class<CategoriesViewModel> =
        CategoriesViewModel::class.java

    override fun initFab() {
        val fab: FloatingActionButton = binding.fab
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fab.tooltipText = resources.getString(R.string.write_a_post)
        }
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_to_create_post)
        }
    }

    override fun initRv() {
        val recyclerView: RecyclerView = binding.recycleviewPosts
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = postListAdapter
    }

    override fun initSwipeRefreshLayout() {
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            viewModel!!.loadPosts(category)
            swipeRefreshLayout.isRefreshing = false
        }
    }


    companion object {
        fun newInstance(arg: String) =
            CategoryPostFragment().apply {
                arguments = Bundle().apply {
                    putString("category", arg)
                }
            }
    }
}