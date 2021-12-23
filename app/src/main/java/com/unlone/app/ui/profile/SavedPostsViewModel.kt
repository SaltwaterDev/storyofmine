package com.unlone.app.ui.profile

import com.unlone.app.model.Post
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.unlone.app.model.PostItemUiState
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.collections.ArrayList

class SavedPostsViewModel : ViewModel() {
    private val _posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val posts: LiveData<List<Post>> = _posts
    private val mPosts = 100
    private val postList: MutableList<Post> = java.util.ArrayList()
    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()
    private var lastVisible: DocumentSnapshot? = null
    val postListUiItems = posts.map { posts ->
        posts.map {
            PostItemUiState(
                it.title,
                it.imagePath,
                it.journal.substring(0,120.coerceAtMost(it.journal.length)),
                it.pid
            )
        }
    }

    init {
        val uid = mAuth.uid     // TODO set up a Room database to store user data
        loadPosts(mPosts)
    }

    // retrieve the list of saved posts
    private suspend fun savedPostsReference(numberPost: Int): List<DocumentSnapshot> {
        return mFirestore.collection("users").document(mAuth.uid!!)
            .collection("saved")
            .limit(numberPost.toLong())
            .get()
            .await()
            .documents
    }

    fun loadPosts(numberPost: Int) { // this: CoroutineScope
        val savedPostList: ArrayList<String> = ArrayList()
        viewModelScope.launch (Dispatchers.IO){ // launch a new coroutine and continue
            val savedPostsReference = async { savedPostsReference(numberPost) }
            for (document in savedPostsReference.await()) {
                savedPostList.add(document.id)
            }
            Log.d("TAG","savedPostList: $savedPostList") // main coroutine continues while a previous one is delayed

            for (pid in savedPostList) {
                val post = mFirestore.collection("posts")
                    .document(pid)
                    .get()
                    .await()
                    .toObject<Post>()

                if (post != null) {
                    post.pid = pid
                    withContext(Dispatchers.Main) {
                        if (!postList.contains(post)) {
                            postList.add(post)
                        }
                    }
                }
            }
            _posts.postValue(postList)
            Log.d(ContentValues.TAG, "added posts: ${posts.value}")
            Log.d(ContentValues.TAG, "added postList: $postList")
        }
    }

}