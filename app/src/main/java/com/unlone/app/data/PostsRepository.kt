package com.unlone.app.data

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.unlone.app.model.Post
import kotlinx.coroutines.tasks.await
import java.util.ArrayList

class PostsRepository {
    private val mAuth = Firebase.auth
    private val mFirestore = Firebase.firestore
    private var lastVisible: DocumentSnapshot? = null
    private val mPosts = 100

    suspend fun loadAllPosts(numberPost: Int = mPosts): List<Post> {
        val allDocs = mFirestore.collection("posts")
            .orderBy("createdTimestamp", Query.Direction.DESCENDING)
            .limit(numberPost.toLong())
            .get()
            .await()

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


}