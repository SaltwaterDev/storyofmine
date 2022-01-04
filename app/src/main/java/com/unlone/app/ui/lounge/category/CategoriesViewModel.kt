package com.unlone.app.ui.lounge.category

import android.util.Log
import androidx.lifecycle.*
import com.unlone.app.data.CategoriesRepository
import com.unlone.app.data.PostsRepository
import com.unlone.app.model.Post
import com.unlone.app.model.PostItemUiState
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel() {
    private val _categories: MutableLiveData<List<String>> = MutableLiveData()
    val categories: LiveData<List<String>> = _categories
    private var _categoryTitle = MutableLiveData<String>()
    val categoryTitle: LiveData<String> = _categoryTitle

    private val categoriesRepository: CategoriesRepository = CategoriesRepository()
    private val postsRepository: PostsRepository = PostsRepository()

    private val _posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val posts: LiveData<List<Post>> = _posts
    val postListUiItems = posts.map { posts ->
        posts.map {
            PostItemUiState(
                it.title,
                it.imagePath,
                it.journal.substring(0, 120.coerceAtMost(it.journal.length)),
                it.pid
            )
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = categoriesRepository.loadCategories()
        }
    }

    fun loadPosts(category: String, numberPost: Int, loadMore: Boolean?) {
        Log.d("TAG", "category: $category")
        viewModelScope.launch {
            _posts.value = postsRepository.getSingleCategoryPosts(category, numberPost)
        }
    }

    fun searchPost(text: String) {
        // TODO ("After using firebase function")
    }

    fun followCategory(category: String, follow: Boolean) {
        categoriesRepository.followCategory(category, follow)
    }

    fun retrieveDefaultCategory(selectedCategory: String): String? {
        return categoriesRepository.retrieveDefaultCategory(selectedCategory)
    }

    fun getCategoryTitle(categoryId: String) {
        viewModelScope.launch {
            _categoryTitle.value = categoriesRepository.getCategoryTitle(categoryId)
        }
    }

    suspend fun isFollowing(category: String): Boolean {
        return categoriesRepository.isFollowing(category)
    }


}