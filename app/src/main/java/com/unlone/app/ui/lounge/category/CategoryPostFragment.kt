package com.unlone.app.ui.lounge.category

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.unlone.app.R
import com.unlone.app.databinding.FragmentCategoryPostBinding
import com.unlone.app.ui.lounge.LoungePostsBaseFragment
import com.unlone.app.utils.CategoryPostViewModelAssistedFactory
import com.unlone.app.utils.ViewModelFactory
import com.unlone.app.viewmodel.CategoryPostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoryPostFragment :
    LoungePostsBaseFragment<FragmentCategoryPostBinding, CategoryPostViewModel>(R.layout.fragment_category_post) {
    private val args: CategoryPostFragmentArgs by navArgs()

    // this is category key
    private val topic by lazy { args.topic }

    // declare viewModel
    @Inject
    lateinit var assistedFactory: CategoryPostViewModelAssistedFactory
    private lateinit var viewModelFactory: ViewModelFactory
    private val ctgPostViewModel: CategoryPostViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CategoryPostViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        super.onCreateView(inflater, container, savedInstanceState)
        viewModelFactory = ViewModelFactory(topic = topic, cpAssistedFactory = assistedFactory)
        ctgPostViewModel.let { vm -> binding.followBtn.setOnClickListener { vm.followCategory() } }
        binding.viewModel = ctgPostViewModel
        binding.lifecycleOwner = this


        // load posts
        val recyclerView: RecyclerView = binding.recycleviewPosts
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = postListAdapter

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }


    override fun getViewModelClass(): Class<CategoryPostViewModel> =
        CategoryPostViewModel::class.java

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                ctgPostViewModel.postListUiItems.collect { uiState ->
                    Log.d("TAG", "uiState: $uiState")
                    postListAdapter.submitList(uiState)
                }
            }
        }
    }

    override fun initSwipeRefreshLayout() {
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            ctgPostViewModel.loadPosts(topic)
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