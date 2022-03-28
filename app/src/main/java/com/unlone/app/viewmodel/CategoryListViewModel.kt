package com.unlone.app.viewmodel

import androidx.lifecycle.*
import com.unlone.app.data.CategoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {
    private val _categories: MutableLiveData<List<String>> = MutableLiveData()
    val categories: LiveData<List<String>> = _categories

    val followingCategories: LiveData<List<String>> = categoriesRepository.loadFollowingTopics().filterNotNull().asLiveData()


    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoriesRepository.categories.collect{
                Timber.d(it.toString())
                _categories.value = it
            }
        }
    }

    fun retrieveDefaultCategory(selectedCategory: String): Flow<String> {
        return categoriesRepository.retrieveDefaultTopic(selectedCategory)
    }

}