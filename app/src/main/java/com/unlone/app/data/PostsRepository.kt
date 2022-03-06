package com.unlone.app.data

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.unlone.app.data.database.PostDao
import com.unlone.app.data.database.asDatabasePost
import com.unlone.app.data.database.asPost
import com.unlone.app.model.Post
import com.unlone.app.model.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsRepository @Inject constructor(
    private val postDao: PostDao,
) {

    private var lastVisible: DocumentSnapshot? = null
    private val mPosts: Int = 100
    private val mAuth: FirebaseAuth = Firebase.auth
    private val mFirestore: FirebaseFirestore = Firebase.firestore

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


    fun getSingleCategoryPosts(
        categoryKey: String
    ): Flow<List<Post>> = postDao.getPostsByCtg(categoryKey)
        .map {
            Log.d("TAG", "dao: $categoryKey")
            it.sortedByDescending { it1 -> it1.createdTimestamp }.map { it1 -> it1.asPost() }
        }


    suspend fun storeSingleCategoryPosts(
        categoryKey: String,
        numberPost: Int = mPosts
    ) {
        Log.d("TAG", "category key: $categoryKey")
        val thisCategoryDocs =
            withContext(Dispatchers.IO) {
                mFirestore.collection("posts")
                    .whereEqualTo("category", categoryKey)
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
        Log.d("TAG", "postList: $postList")
        postDao.insertAll(postList.map { it.asDatabasePost() })

    }

    suspend fun getSingleLabelPosts(
        labelText: String,
        numberPost: Int = mPosts
    ): List<Post> {

        Log.d("TAG", "labelText: $labelText")
        // since that label string is "#xxxx", need to remove "#" first
        val label = labelText.drop(1)
        Log.d("TAG", "label: $label")

        val thisLabelDocs = label.let {
            withContext(Dispatchers.IO) {
                mFirestore.collection("posts")
                    .whereArrayContains("labels", it)
                    .limit(numberPost.toLong())
                    .get()
                    .await()
            }
        }
        val postList: MutableList<Post> = ArrayList()
        if (thisLabelDocs != null) {
            if (thisLabelDocs.size() > 0) {
                lastVisible = thisLabelDocs.documents[thisLabelDocs.size() - 1]
            }
            for (document in thisLabelDocs) {
                Log.d(ContentValues.TAG, document.id + " => " + document.data)
                val post = document.toObject<Post>()
                post.pid = document.id
                if (!postList.contains(post)) {
                    postList.add(post)
                }
            }
        }
        // return the sorted postList
        Log.d("TAG", "postList: $postList")
        return postList.sortedByDescending { it.createdTimestamp }
    }


    suspend fun loadPost(pid: String): Post? = withContext(Dispatchers.IO) {
        val documentSnapshot = mFirestore.collection("posts")
            .document(pid)
            .get()
            .await()
        documentSnapshot.toObject<Post>()
    }

    fun deletePost(pid: String) {
        mFirestore.collection("posts")
            .document(pid)
            .delete()
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
    }

    fun savePost(pid: String): Boolean {
        val timestamp =
            hashMapOf("saveTime" to System.currentTimeMillis().toString())
        mAuth.uid?.let { uid ->
            mFirestore.collection("users").document(uid)
                .collection("saved")
                .document(pid)
                .set(timestamp)
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }
        return true
    }

    fun unsavePost(pid: String): Boolean {
        // User uncheck chose the "Saving" item, save the post...
        mAuth.uid?.let { uid ->
            mFirestore.collection("users").document(uid)
                .collection("saved")
                .document(pid)
                .delete()
        }
        return false
    }

    fun uploadReport(report: Report) {
        mFirestore.collection("reports")
            .add(report)
    }

    suspend fun isSaved(pid: String): Boolean {
        val result = mFirestore.collection("users").document(mAuth.uid!!)
            .collection("saved")
            .document(pid)
            .get()
            .await()
        return (result != null && result.exists())
    }


}