package com.unlone.app.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unlone.app.viewmodel.DetailedPostViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val pid: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailedPostViewModel::class.java)) {
            return DetailedPostViewModel(pid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}