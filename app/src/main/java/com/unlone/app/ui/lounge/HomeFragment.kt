package com.unlone.app.ui.lounge

import android.os.Build
import android.os.Bundle
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
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class HomeFragment : Fragment(), ItemClickListener {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private val homeParentAdapter: HomeParentAdapter by lazy {
        HomeParentAdapter(
            this,
            onClick = { pid -> onClick(pid) },
            onMorePostsClick = { topic -> adapterMorePostsOnClick(topic) })
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
                if (findNavController().currentDestination?.id == R.id.navigation_home)
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
        binding.postPerCategoriesRv.adapter = homeParentAdapter
        binding.progressCircular.visibility = View.VISIBLE
        initFab()
        // binding.greetingTv.text = context?.resources?.getString("Welcome back ")

        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // load topics
                viewModel.displayingTopicsUiState.collect {
                    categoriesAdapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeItemUiStateItems.collect { uiState ->
                    Timber.d("uiState: $uiState")
                    homeParentAdapter.submitList(uiState)
                    binding.progressCircular.visibility = View.GONE
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

    private fun adapterMorePostsOnClick(topic: String) {
        openSpecificTopic(topic)
    }


    // navigate to specific topic
    private fun openSpecificTopic(topic: String) {
        Timber.d("selected topic: $topic")
        if (topic.first() != '#'){
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.retrieveDefaultCategory(topic).collect{
                    val selectedTopic = it
                    if (selectedTopic != "null") {
                        Timber.d("selected topic: $selectedTopic")

                        // open the post with specific category
                        val action =
                            HomeFragmentDirections.actionNavigationHomeToCategoryPostFragment(selectedTopic)
                        findNavController().navigate(action)
                    } else {
                        Timber.d("couldn't find the category")
                    }
                }
            }
        } else{
            if (topic != "null") {
                Timber.d("selected topic: $topic")

                // open the post with specific category
                val action =
                    HomeFragmentDirections.actionNavigationHomeToCategoryPostFragment(topic)
                findNavController().navigate(action)
            } else {
                Timber.d("couldn't find the category")
            }
        }
    }


    // navigate to topic list
    private fun navToLoadMoreTopics() {
        findNavController().navigate(R.id.action_navigation_home_to_categoryListFragment)
    }
}
