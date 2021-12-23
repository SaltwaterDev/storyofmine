package com.unlone.app.ui.lounge.following

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.unlone.app.model.Post
import com.unlone.app.model.PostItemUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class LoungeFollowingViewModel : ViewModel() {
    private val _posts: MutableLiveData<List<Post>> = MutableLiveData()
    val posts: LiveData<List<Post>> = _posts
    private val mPosts = 100
    private val postList: MutableList<Post> = ArrayList()
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
    }


    private suspend fun loadFollowingCategories(): ArrayList<String>? {
        // retrieve the following categories first
        val result = mFirestore.collection("users")
            .document(mAuth.uid!!)
            .get()
            .await()
            .data
            ?.get("followingCategories")
        return if (result != null) result as ArrayList<String> else null
    }

    fun loadPosts(numberPost: Int, loadMore: Boolean?) {

        viewModelScope.launch(Dispatchers.IO) {
            // retrieve the following categories
            val followingCategories = async { loadFollowingCategories() }

            // load the post with the following categories AND self-written posts
            Log.d(ContentValues.TAG, "followingCategories: $followingCategories")
            if (lastVisible == null || !loadMore!!) {
                postList.clear()
                Log.d(ContentValues.TAG, "First load/Refresh")
                // add the following categories
                val followingDocs = followingCategories.await()?.let {
                    mFirestore.collection("posts")
                        .whereIn("category", it)
                        .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                        .limit(numberPost.toLong())
                        .get()
                        .await()
                }

                if (followingDocs != null) {
                    if (followingDocs.size() > 0) {
                        lastVisible = followingDocs.documents[followingDocs.size() - 1]
                    }
                    for (document in followingDocs) {
                        Log.d(ContentValues.TAG, document.id + " => " + document.data)
                        val post = document.toObject<Post>()
                        post.pid = document.id
                        if (!postList.contains(post)) {
                            postList.add(post)
                        }
                    }
                }

                // add the self-written posts
                val selfDocs = mFirestore.collection("posts")
                    .whereEqualTo("author_uid", mAuth.uid)
                    .get()
                    .await()
                for (document in selfDocs) {
                    Log.d(ContentValues.TAG, document.id + " => " + document.data)
                    val post = document.toObject<Post>()
                    post.pid = document.id
                    if (!postList.contains(post)) {
                        postList.add(post)
                    }
                }
                // sort the postList again
                val sortedPostList = postList.sortedByDescending { it.createdTimestamp }
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "sorted postList: $sortedPostList")
                    _posts.value = sortedPostList
                }
            } else {
                // TODO ("fixing the paging")
                // add the following categories
                val followingDocs = followingCategories.await()?.let {
                    mFirestore.collection("posts")
                        .whereIn("category", it)
                        .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                        //.startAfter(lastVisible)
                        .limit(numberPost.toLong())
                        .get()
                        .await()
                }

                followingDocs?.let { results ->
                    if (results.size() > 0) {
                        for (document in results) {
                            Log.d(ContentValues.TAG, document.id + " => " + document.data)
                            val post = document.toObject(Post::class.java)
                            post.pid = document.id
                            if (!postList.contains(post)) {
                                postList.add(post)
                            }
                        }
                        lastVisible = results.documents[results.size() - 1]
                    } else {
                        Log.d(ContentValues.TAG, "End of posts")
                    }
                }

                // add the self-written posts
                val selfDocs = mFirestore.collection("posts")
                    .whereEqualTo("author_id", mAuth.uid)
                    // .startAfter(lastVisible)
                    .get()
                    .await()
                for (document in selfDocs) {
                    Log.d(ContentValues.TAG, document.id + " => " + document.data)
                    val post = document.toObject<Post>()
                    post.pid = document.id
                    if (!postList.contains(post)) {
                        postList.add(post)
                    }
                }
                // sort the postList again
                val sortedPostList = postList.sortedByDescending { it.createdTimestamp }
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "sorted postList: $sortedPostList")
                    _posts.value = sortedPostList
                }
            }
        }
    }

    fun searchPost(text: String) {
        // TODO ("After using firebase function")
    }
}