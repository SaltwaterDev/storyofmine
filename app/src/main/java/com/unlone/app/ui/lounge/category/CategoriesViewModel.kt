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

    private val _followingCategories: MutableLiveData<List<String>> = MutableLiveData()
    val followingCategories: LiveData<List<String>> = _followingCategories
    private var _topicTitle = MutableLiveData<String>()
    val topicTitle: LiveData<String> = _topicTitle
    private var _topicId = MutableLiveData<String>()
    private val topicId: LiveData<String> = _topicId

    private val categoriesRepository: CategoriesRepository = CategoriesRepository()
    private val postsRepository: PostsRepository = PostsRepository()

    private val _posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val posts: LiveData<List<Post>> = topicId.switchMap {
        liveData {
            Log.d("TAG", "label to search: $it")
            if (it.first() != '#')
                emit(postsRepository.getSingleCategoryPosts(it))
            else
                emit(postsRepository.getSingleLabelPosts(it))
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


    fun loadPosts(topic: String, loadMore: Boolean? = false) {
        viewModelScope.launch {
            if (topic.first() != '#')
                _posts.value = postsRepository.getSingleCategoryPosts(topic)
            else
                _posts.value = postsRepository.getSingleLabelPosts(topic)
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

    fun getTopicTitle(topicId: String) {
        Log.d("TAG", "setCategoryTitle: $topicId")
        _topicId.value = topicId
        if (topicId.first() != '#') {
            viewModelScope.launch {
                _topicTitle.value = categoriesRepository.getTopicTitle(topicId)
            }
        } else {
            _topicTitle.value = topicId
        }
    }

    suspend fun isFollowing(category: String): Boolean {
        return categoriesRepository.isFollowing(category)
    }


}