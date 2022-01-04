package com.unlone.app.ui.lounge.all

import androidx.lifecycle.*
import com.unlone.app.data.PostsRepository
import com.unlone.app.model.Post
import com.unlone.app.model.PostItemUiState
import kotlinx.coroutines.launch


class LoungeAllViewModel : ViewModel() {
    private val _posts: MutableLiveData<List<Post>> = MutableLiveData()
    val posts: LiveData<List<Post>> = _posts
    private val mPosts = 100
    private val repository = PostsRepository()
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
        loadPosts(mPosts)
    }

    fun loadPosts(numberPost: Int = mPosts) {
        viewModelScope.launch {
            _posts.value = repository.loadAllPosts(numberPost)
        }
    }


    fun searchPost(text: String) {
        // TODO ("After using firebase function")
    }

}