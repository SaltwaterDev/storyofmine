package com.unlone.app.ui.lounge.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.unlone.app.instance.Post
import androidx.lifecycle.LiveData
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class LoungeAllViewModel : ViewModel() {
    val posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val postList: MutableList<Post>
    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore: FirebaseFirestore
    private var lastVisible: DocumentSnapshot? = null
    fun getPosts(): LiveData<List<Post>> {
        return posts
    }

    init {
        val uid = mAuth.uid
        postList = ArrayList()
        mFirestore = FirebaseFirestore.getInstance()
    }

    fun loadPosts(numberPost: Int, loadMore: Boolean?) {

        viewModelScope.launch(Dispatchers.IO) {
            if (lastVisible == null || !loadMore!!) {
                postList.clear()
                Log.d(ContentValues.TAG, "First load/Refresh")

                val allDocs = mFirestore.collection("posts")
                    .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                    .limit(numberPost.toLong())
                    .get()
                    .await()

                if (allDocs != null) {
                    if (allDocs.size() > 0) {
                        lastVisible = allDocs.documents[allDocs.size() - 1]
                    }
                    for (document in allDocs) {
                        Log.d(ContentValues.TAG, document.id + " => " + document.data)
                        val post = document.toObject<Post>()
                        post.pid = document.id
                        if (!postList.contains(post)) {
                            postList.add(post)
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "postList: $postList")
                    posts.value = postList
                }
            } else {
                val allDocs = mFirestore.collection("posts")
                    .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                    //.startAfter(lastVisible)
                    .limit(numberPost.toLong())
                    .get()
                    .await()

                if (allDocs != null) {
                    if (allDocs.size() > 0) {
                        lastVisible = allDocs.documents[allDocs.size() - 1]
                    }
                    for (document in allDocs) {
                        Log.d(ContentValues.TAG, document.id + " => " + document.data)
                        val post = document.toObject<Post>()
                        post.pid = document.id
                        if (!postList.contains(post)) {
                            postList.add(post)
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "postList: $postList")
                    posts.value = postList
                }
            }
        }
    }

    fun searchPost(text: String) {
        // TODO ("After using firebase function")
    }
}