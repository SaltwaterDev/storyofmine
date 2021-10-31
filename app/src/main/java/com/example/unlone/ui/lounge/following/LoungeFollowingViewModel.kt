package com.example.unlone.ui.lounge.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.unlone.instance.Post
import androidx.lifecycle.LiveData
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class LoungeFollowingViewModel : ViewModel() {
    val posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val postList: MutableList<Post>
    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore: FirebaseFirestore
    private var lastVisible: DocumentSnapshot? = null
    fun getPosts(): LiveData<List<Post>> {
        return posts
    }

    init {
        val uid = mAuth.uid     // TODO set up a Room database to store user data
        postList = ArrayList()
        mFirestore = FirebaseFirestore.getInstance()
    }


    private suspend fun loadFollowingCategories(): ArrayList<String>? {
        // retrieve the following categories first
        val result =  mFirestore.collection("users")
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
                Log.d(ContentValues.TAG, "First load")
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
                withContext(Dispatchers.Main){
                    Log.d("TAG", "sorted postList: $sortedPostList")
                    posts.value = sortedPostList
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

                followingDocs?.let{ results ->
                    if (results.size() > 0) {
                        for (document in results) {
                            Log.d(ContentValues.TAG, document.id + " => " + document.data)
                            val post = document.toObject(Post::class.java)
                            post.pid = document.id
                            if (!postList.contains(post)) {
                                postList.add(post)
                            }
                        }
                        lastVisible =
                            results.documents[results.size() - 1]
                    } else {Log.d(ContentValues.TAG, "End of posts")}
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
                withContext(Dispatchers.Main){
                    Log.d("TAG", "sorted postList: $sortedPostList")
                    posts.value = sortedPostList
                }

            }
        }
    }

    fun searchPost(text: String){
        // TODO ("After using firebase function")
    }
}