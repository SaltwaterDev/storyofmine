package com.unlone.app.ui.lounge.common

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class LoungePostsBaseFragment<T : ViewDataBinding, VM : ViewModel>(@LayoutRes private val layoutResId : Int) : Fragment(){

    private var _binding : T? = null
    var viewModel : VM? = null
    val postsAdapter: PostsAdapter by lazy {PostsAdapter()}
    val mPosts = 100
    private var isLoading = false

    val binding : T get() = _binding!!

    protected abstract fun getViewModelClass(): Class<VM>

    // Make it open, so it can be overridden in child fragments
    open fun T.initialize(){
        initFab()
        initRv()
        initSwipeRefreshLayout()
        initSearchBar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        viewModel = ViewModelProvider(this).get(getViewModelClass())
        binding.initialize()
        return binding.root
    }


    // Removing the binding reference when not needed is recommended as it avoids memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun initFab()
    abstract fun initRv()
    abstract fun initSwipeRefreshLayout()
    abstract fun initSearchBar()
}


