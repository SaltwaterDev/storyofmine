package com.unlone.app.data

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import com.unlone.app.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class PostsRepository {
    private val mAuth = Firebase.auth
    private val mFirestore = Firebase.firestore
    private var lastVisible: DocumentSnapshot? = null
    private val mPosts = 100
    private val categoriesRepository: CategoriesRepository = CategoriesRepository()

    suspend fun loadAllPosts(numberPost: Int = mPosts): List<Post> {
        val allDocs = withContext(Dispatchers.IO) {
            mFirestore.collection("posts")
                .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                .limit(numberPost.toLong())
                .get()
                .await()
        }

        val postList: MutableList<Post> = ArrayList()

        allDocs?.let { it ->
            if (allDocs.size() > 0) {
                lastVisible = allDocs.documents[allDocs.size() - 1]
            }
            for (document in it) {
                Log.d(ContentValues.TAG, document.id + " => " + document.data)
                val post = document.toObject<Post>()
                post.pid = document.id
                if (!postList.contains(post)) {
                    postList.add(post)
                }
            }
        }
        // sort the postList
        return postList.sortedByDescending { it.createdTimestamp }
    }

    private suspend fun loadFollowingCategories(): ArrayList<String>? {
        // retrieve the following categories first
        val result = withContext(Dispatchers.IO) {
            mFirestore.collection("users")
                .document(mAuth.uid!!)
                .get()
                .await()
                .data
                ?.get("followingCategories")
        }
        return if (result != null) result as ArrayList<String> else null
    }

    suspend fun getCategoriesAndSelfPosts(numberPost: Int = mPosts): List<Post> {

        // retrieve the following categories
        val followingCategories = withContext(Dispatchers.IO) {
            async { loadFollowingCategories() }
        }
        // load the post with the following categories AND self-written posts
        Log.d(ContentValues.TAG, "followingCategories: $followingCategories")

        val postList: MutableList<Post> = ArrayList()
        // add the following categories
        val followingDocs = withContext(Dispatchers.IO) {
            followingCategories.await()?.let {
                mFirestore.collection("posts")
                    .whereIn("category", it)
                    .limit(numberPost.toLong())
                    .get()
                    .await()
            }
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
        return postList.sortedByDescending { it.createdTimestamp }
    }

    suspend fun getSingleCategoryPosts(
        category: String,
        numberPost: Int = mPosts
    ): List<Post> {
        Log.d("TAG", "category: $category")

        val thisCategoryDocs = withContext(Dispatchers.IO) {
            mFirestore.collection("posts")
                .whereEqualTo("category", category)
                .limit(numberPost.toLong())
                .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                .get()
                .await()
        }
        val postList: MutableList<Post> = ArrayList()
        if (thisCategoryDocs != null) {
            if (thisCategoryDocs.size() > 0) {
                lastVisible = thisCategoryDocs.documents[thisCategoryDocs.size() - 1]
            }
            for (document in thisCategoryDocs) {
                Log.d(ContentValues.TAG, document.id + " => " + document.data)
                val post = document.toObject<Post>()
                post.pid = document.id
                if (!postList.contains(post)) {
                    postList.add(post)
                }
            }
        }
        // return the sorted postList
        return postList.sortedByDescending { it.createdTimestamp }
    }

}