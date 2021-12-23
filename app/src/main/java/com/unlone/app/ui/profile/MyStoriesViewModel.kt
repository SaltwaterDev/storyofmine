package com.unlone.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.unlone.app.model.Post
import androidx.lifecycle.LiveData
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.unlone.app.model.PostItemUiState
import java.util.*

class MyStoriesViewModel : ViewModel() {
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
        loadPosts(mPosts, false)
    }

    fun loadPosts(numberPost: Int, loadMore: Boolean?) {
        if (lastVisible == null || !loadMore!!) {
            postList.clear()
            Log.d(ContentValues.TAG, "First load")
            mFirestore.collection("posts")
                .whereEqualTo("author_uid", mAuth.uid!!)
                .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                .limit(numberPost.toLong())
                .get()
                .addOnCompleteListener { task ->
                    task.result.let { results ->
                        if (results.size() > 0) {
                            lastVisible = results.documents[results.size() - 1]
                        }
                        for (document in results) {
                            Log.d(ContentValues.TAG, document.id + " => " + document.data)
                            val post = document.toObject<Post>()
                            post.pid = document.id
                            if (!postList.contains(post)) {
                                postList.add(post)
                                _posts.value = postList
                            }
                        }
                    }
                }
        } else {
            mFirestore.collection("posts")
                .whereEqualTo("author_uid", mAuth.uid!!)
                .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(numberPost.toLong())
                .get()
                .addOnCompleteListener { task ->
                    task.result.let { results ->
                        if (results.size() > 0) {
                            for (document in results) {
                                Log.d(ContentValues.TAG, document.id + " => " + document.data)
                                val post = document.toObject(Post::class.java)
                                post.pid = document.id
                                if (!postList.contains(post)) {
                                    postList.add(post)
                                    _posts.value = postList
                                }
                            }
                            lastVisible =
                                results.documents[task.result.size() - 1]
                        } else {
                            Log.d(ContentValues.TAG, "End of posts")
                        }
                    }

                }
        }
    }

    fun searchPost(text: String) {
        // TODO ("After using firebase function")
    }
}