package com.unlone.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.data.CategoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {
    private val _categories: MutableLiveData<List<String>> = MutableLiveData()
    val categories: LiveData<List<String>> = _categories

    private val _followingCategories: MutableLiveData<List<String>> = MutableLiveData()
    val followingCategories: LiveData<List<String>> = _followingCategories


    init {
        loadCategories()
        loadFollowingCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = categoriesRepository.loadCategories()
        }
    }

    private fun loadFollowingCategories() {
        viewModelScope.launch {
            _followingCategories.value = categoriesRepository.loadFollowingTopics().filterNotNull()
        }
    }

    fun retrieveDefaultCategory(selectedCategory: String): String? {
        return categoriesRepository.retrieveDefaultTopic(selectedCategory)
    }

}