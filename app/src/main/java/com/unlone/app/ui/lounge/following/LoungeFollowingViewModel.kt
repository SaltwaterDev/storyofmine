package com.unlone.app.ui.lounge.following

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.unlone.app.data.PostsRepository
import com.unlone.app.model.Post
import com.unlone.app.model.PostItemUiState
import kotlinx.coroutines.launch

class LoungeFollowingViewModel : ViewModel() {
    private val _posts: MutableLiveData<List<Post>> = MutableLiveData()
    val posts: LiveData<List<Post>> = _posts
    private val repository = PostsRepository()
    private val mAuth = FirebaseAuth.getInstance()
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
        val uid = mAuth.uid     // TODO set up a Room database to store user data
    }


    fun loadPosts(numberPost: Int) {
        viewModelScope.launch {
            _posts.value = repository.getCategoriesAndSelfPosts(numberPost)
        }
    }

    fun searchPost(text: String) {
        // TODO ("After using firebase function")
    }
}