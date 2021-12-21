package com.unlone.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.unlone.app.model.Post
import androidx.lifecycle.LiveData
import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import java.util.*

class MyStoriesViewModel : ViewModel() {
    val posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val postList: MutableList<Post>
    private val mFirestore: FirebaseFirestore
    val mAuth = FirebaseAuth.getInstance()
    private var lastVisible: DocumentSnapshot? = null
    fun getPosts(): LiveData<List<Post>> {
        return posts
    }

    init {
        postList = ArrayList()
        mFirestore = FirebaseFirestore.getInstance()
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
                                posts.value = postList
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
                                    posts.value = postList
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