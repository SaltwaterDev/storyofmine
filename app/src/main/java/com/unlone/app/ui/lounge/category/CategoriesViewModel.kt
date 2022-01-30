package com.unlone.app.ui.lounge.category

import androidx.lifecycle.*
import com.unlone.app.data.CategoriesRepository
import com.unlone.app.data.PostsRepository
import com.unlone.app.model.Post
import com.unlone.app.model.PostItemUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel() {
    private val _categories: MutableLiveData<List<String>> = MutableLiveData()
    val categories: LiveData<List<String>> = _categories

    private val _followingCategories: MutableLiveData<List<String>> = MutableLiveData()
    val followingCategories: LiveData<List<String>> = _followingCategories
    private var _categoryTitle = MutableLiveData<String>()
    val categoryTitle: LiveData<String> = _categoryTitle

    private val categoriesRepository: CategoriesRepository = CategoriesRepository()
    private val postsRepository: PostsRepository = PostsRepository()

    private val _posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val posts: LiveData<List<Post>> = categoryTitle.switchMap {
        liveData {
            retrieveDefaultCategory(it)?.let { it1 ->
                val isLabel = it1.first() == '#'
                if (!isLabel)
                    emit(postsRepository.getSingleCategoryPosts(it1))
                else
                    emit(postsRepository.getSingleLabelPosts(it1))

            }
        }
    }

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

    init {
        _categoryTitle.value?.let { loadPosts(it) }
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
            _followingCategories.value = categoriesRepository.loadFollowingTopics()
        }
    }

    fun loadPosts(category: String, loadMore: Boolean? = false) {
        val isLabel = category.first() == '#'
        viewModelScope.launch {
            if (!isLabel)
                _posts.value = postsRepository.getSingleCategoryPosts(category)
            else
                _posts.value = postsRepository.getSingleLabelPosts(category)
        }
    }

    fun searchPost(text: String) {
        // TODO ("After using firebase function")
    }

    fun followCategory(category: String, follow: Boolean) {
        categoriesRepository.followCategory(category, follow)
    }

    fun retrieveDefaultCategory(selectedCategory: String): String? {
        return categoriesRepository.retrieveDefaultTopic(selectedCategory)
    }

    fun getCategoryTitle(categoryId: String) {
        viewModelScope.launch {
            _categoryTitle.value = categoriesRepository.getTopicTitle(categoryId)
        }
    }

    suspend fun isFollowing(category: String): Boolean {
        return categoriesRepository.isFollowing(category)
    }


}