package com.unlone.app.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unlone.app.viewmodel.CategoryPostViewModel
import com.unlone.app.viewmodel.DetailedPostViewModel
import dagger.assisted.AssistedFactory


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val pid: String? = null,
    private val dpAssistedFactory: DetailedPostViewModelAssistedFactory? = null,
    private val topic: String? = null,
    private val cpAssistedFactory: CategoryPostViewModelAssistedFactory? = null,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailedPostViewModel::class.java)) {
            return pid?.let { dpAssistedFactory?.create(it) } as T
        } else if (modelClass.isAssignableFrom(CategoryPostViewModel::class.java)) {
            return topic?.let { cpAssistedFactory?.create(it) } as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@AssistedFactory
interface DetailedPostViewModelAssistedFactory {
    fun create(pid: String): DetailedPostViewModel
}

@AssistedFactory
interface CategoryPostViewModelAssistedFactory {
    fun create(topic: String): CategoryPostViewModel
}