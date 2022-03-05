package com.unlone.app.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.switchMap
import com.unlone.app.data.CategoriesRepository
import com.unlone.app.data.PostsRepository
import com.unlone.app.model.Post
import com.unlone.app.model.PostItemUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryPostViewModel @AssistedInject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val postsRepository: PostsRepository,
    @Assisted private val topicId: String,
) : ViewModel() {

    val topicTitle: StateFlow<String?> =
        getTopicTitle(topicId).stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _isFollowing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing


    private val _posts: MutableStateFlow<List<Post>?> = MutableStateFlow(emptyList())
    val posts: StateFlow<List<Post>?> = _posts


    val postListUiItems: StateFlow<List<PostItemUiState>?> = posts.map { posts ->
        posts?.map {
            PostItemUiState(
                it.title,
                it.imagePath,
                it.journal.substring(0, 120.coerceAtMost(it.journal.length)),
                it.pid
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    init {
        viewModelScope.launch {
            if (topicId.first() != '#')
                postsRepository.getSingleCategoryPosts(topicId).collect {
                    _posts.value = it
                }
            else
                _posts.value = postsRepository.getSingleLabelPosts(topicId)

            _isFollowing.value = categoriesRepository.isFollowing(topicId)
        }
    }


    fun searchPost(text: String) {
        // TODO ("After using firebase function")
    }

    fun followCategory() {
        categoriesRepository.followCategory(topicId, !isFollowing.value)
        _isFollowing.value = !isFollowing.value
        Log.d("TAG", "followCategory: ${isFollowing.value}")
    }


    private fun getTopicTitle(topicId: String) = flow {
        Log.d("TAG", "setCategoryTitle: $topicId")
        if (topicId.first() != '#') {
            categoriesRepository.getTopicTitle(topicId)
        } else {
            emit(topicId)
        }
    }

    fun loadPosts(category: String) {
        // TODO ("Not yet implement")
    }


}