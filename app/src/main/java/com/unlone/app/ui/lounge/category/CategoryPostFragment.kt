package com.unlone.app.ui.lounge.category

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.unlone.app.R
import com.unlone.app.databinding.FragmentCategoryPostBinding
import com.unlone.app.ui.lounge.common.LoungePostsBaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryPostFragment :
    LoungePostsBaseFragment<FragmentCategoryPostBinding, CategoriesViewModel>(R.layout.fragment_category_post) {
    private val args: CategoryPostFragmentArgs by navArgs()
    private val category by lazy {
        args.category
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This callback will only be called when CategoryPostFragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            view?.let {
                Navigation.findNavController(it).navigate(R.id.navigateToCategoryListFragment)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        super.onCreateView(inflater, container, savedInstanceState)
        // set category title
        setCategoryTitle()
        isFollowing(binding.isFollowingTv)
        initFollowingButton()

        // load posts
        val recyclerView: RecyclerView = binding.recycleviewPosts
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = postsAdapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // load posts
        // TODO (move this function into viewModel)
        viewModel!!.postListUiItems.observe(
            viewLifecycleOwner
        ) { postList ->
            postsAdapter.submitList(postList)
        }
        viewModel!!.loadPosts(category, mPosts, false)
    }

    private fun initFollowingButton() {
        binding.isFollowingTv.setOnClickListener {
            if (binding.isFollowingTv.tag == "follow") {
                // set follow to true to follow this category
                viewModel!!.followCategory(category, true)
                binding.isFollowingTv.tag = "following"
                binding.isFollowingTv.text = getString(R.string.following)
            } else {
                // set follow to false to unfollow this category
                viewModel!!.followCategory(category, false)
                binding.isFollowingTv.tag = "follow"
                binding.isFollowingTv.text = getString(R.string.follow)
            }
        }
    }

    private fun setCategoryTitle() {
        viewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)
        viewModel?.getCategoryTitle(args.category)
        viewModel?.categoryTitle?.observe(viewLifecycleOwner) { title ->
            binding.categoryTitleTv.text = title
        }
    }

    private fun isFollowing(followingTv: TextView) {
        lifecycleScope.launch(Dispatchers.IO) {
            val isFollowing = viewModel?.isFollowing(category) == true
            withContext(Dispatchers.Main) {
                if (isFollowing) {
                    followingTv.text = getString(R.string.following)
                    followingTv.tag = "following"
                } else {
                    followingTv.text = getString(R.string.follow)
                    followingTv.tag = "follow"
                }
            }
        }
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
        recyclerView.adapter = postsAdapter
    }

    override fun initSwipeRefreshLayout() {
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            viewModel!!.loadPosts(category, mPosts, false)
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