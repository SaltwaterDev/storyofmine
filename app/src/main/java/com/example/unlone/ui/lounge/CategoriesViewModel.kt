package com.example.unlone.ui.lounge

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unlone.R
import com.example.unlone.instance.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import java.util.*

class CategoriesViewModel: ViewModel() {
    private var _categories: MutableLiveData<List<String>> = MutableLiveData()
    var categories:  LiveData<List<String>> = _categories

    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val mAuth = FirebaseAuth.getInstance()
    private val postList: MutableList<Post>
    private var lastVisible: DocumentSnapshot? = null
    fun getPosts(): LiveData<List<Post>> {
        return posts
    }

    @Suppress("UNCHECKED_CAST")
    fun loadCategories() {
        mFirestore.collection("categories")
            .document("pre_defined_categories")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val data: Map<String, Any> = documentSnapshot.data as Map<String, Any>
                _categories.value = data["list"] as List<String>?
                Log.d("TAG category", _categories.toString())
            }
    }



    init {
        postList = ArrayList()
    }

    fun loadPosts(category: String, numberPost: Int, loadMore: Boolean?) {
        if (lastVisible == null || !loadMore!!) {
            postList.clear()
            Log.d(TAG, "First load")
            mFirestore.collection("posts")
                .whereEqualTo("category", category)
                .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                .limit(numberPost.toLong())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let{ results ->
                            if (results.size() > 0) {
                                lastVisible = results.documents[results.size() - 1]
                            }
                            for (document in results) {
                                Log.d(TAG, document.id + " => " + document.data)
                                val post = document.toObject<Post>()
                                post.pid = document.id
                                if (!postList.contains(post)) {
                                    postList.add(post)
                                    posts.value = postList
                                }
                            }
                        }
                    }else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }
        } else {
            mFirestore.collection("posts")
                .whereEqualTo("category", category)
                .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(numberPost.toLong())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let{ results ->
                            if (results.size() > 0) {
                                for (document in results) {
                                    Log.d(TAG, document.id + " => " + document.data)
                                    val post = document.toObject(Post::class.java)
                                    post.pid = document.id
                                    if (!postList.contains(post)) {
                                        postList.add(post)
                                        posts.value = postList
                                    }
                                }
                                lastVisible =
                                    results.documents[task.result!!.size() - 1]
                            } else {
                                Log.d(TAG, "End of posts")}
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }
        }
    }

    fun searchPost(text: String){
        // TODO ("After using firebase function")
    }


    fun followCategory(category: String, follow: Boolean){
        Log.d("TAG", "category: $category")
        mAuth.uid?.let {
            if(follow){
                mFirestore.collection("users").document(it)
                    .update(
                        "followingCategories", FieldValue.arrayUnion(category)
                    )
            } else{
                    mFirestore.collection("users").document(it)
                        .update(
                            "followingCategories", FieldValue.arrayRemove(category)
                        )
            }
        }
    }
}