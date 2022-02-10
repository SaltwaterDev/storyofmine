package com.unlone.app.ui.lounge

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.unlone.app.MobileNavigationDirections
import com.unlone.app.R
import com.unlone.app.databinding.FragmentHomeBinding
import com.unlone.app.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class HomeFragment : Fragment(), ItemClickListener {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private val parentPostsAdapter: ParentPostsAdapter by lazy {
        ParentPostsAdapter(
            this
        ) { pid -> onClick(pid) }
    }
    private val categoriesAdapter: CategoriesAdapter by lazy {
        CategoriesAdapter { titleId ->
            adapterTopicOnClick(
                titleId
            )
        }
    }

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when Fragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                context?.let {
                    MaterialAlertDialogBuilder(it, R.style.ThemeOverlay_App_MaterialAlertDialog)
                        .setTitle(it.getString(R.string.reminding))
                        .setMessage(it.getString(R.string.leaving_app))
                        .setPositiveButton(it.getString(R.string.proceed))
                        { _, _ ->
                            activity?.finish()
                        }
                        .show()
                }
            }
        })
        // The callback can be enabled or disabled here or in the lambda
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.categoriesListRv.adapter = categoriesAdapter
        binding.postPerCategoriesRv.adapter = parentPostsAdapter
        initFab()

        // load topics
        viewModel.displayingTopicsUiState.observe(viewLifecycleOwner) {
            categoriesAdapter.submitList(it)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // Note that this happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                viewModel.parentPostItemUiStateItems.collect { uiState ->
                    // New value received
                    Log.d("TAG", "uiState: $uiState")
                    parentPostsAdapter.submitList(uiState.filter { it?.postsUiStateItemList?.isNotEmpty()
                        ?: it == true  })
                }
            }
        }

        return view
    }



    private fun initFab() {
        val fab: FloatingActionButton = binding.fab
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fab.tooltipText = resources.getString(R.string.write_a_post)
        }
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_to_create_post)
        }
    }

    override fun onClick(pid: String) {
        val action = MobileNavigationDirections.actionGlobalPostDetailFragment(pid)
        view?.findNavController()?.navigate(action)
    }

    private fun adapterTopicOnClick(topic: String) {
        if (topic == R.string.topic_more.toString()) navToLoadMoreTopics() else openSpecificTopic(
            topic
        )
    }


    // navigate to specific topic
    private fun openSpecificTopic(topic: String) {
        Log.d("TAG", "selected topic: $topic")
        val selectedTopic = if (topic.first() != '#') viewModel.retrieveDefaultCategory(topic).toString() else topic
        if (selectedTopic != "null"){
            // open the post with specific category
            val action = HomeFragmentDirections.actionNavigationHomeToCategoryPostFragment(selectedTopic)
            findNavController().navigate(action)
        }
        else{
            Log.d("TAG", "couldn't find the category")
        }
    }

    // navigate to topic list
    private fun navToLoadMoreTopics() {
        findNavController().navigate(R.id.action_navigation_home_to_categoryListFragment)
    }
}
