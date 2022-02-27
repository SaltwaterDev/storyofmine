package com.unlone.app.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unlone.app.viewmodel.DetailedPostViewModel
import dagger.assisted.AssistedFactory


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val pid: String,
    private val assistedFactory: DetailedPostViewModelAssistedFactory,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailedPostViewModel::class.java)) {
            return assistedFactory.create(pid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@AssistedFactory
interface DetailedPostViewModelAssistedFactory {
    fun create(pid: String): DetailedPostViewModel

}