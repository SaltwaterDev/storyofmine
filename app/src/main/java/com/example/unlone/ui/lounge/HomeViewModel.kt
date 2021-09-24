package com.example.unlone.ui.lounge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.unlone.instance.Post
import androidx.lifecycle.LiveData
import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import java.util.*

class HomeViewModel : ViewModel() {
    val posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val postList: MutableList<Post>
    private val mFirestore: FirebaseFirestore
    private var lastVisible: DocumentSnapshot? = null
    fun getPosts(): LiveData<List<Post>> {
        return posts
    }

    init {
        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.uid
        postList = ArrayList()
        mFirestore = FirebaseFirestore.getInstance()
    }

    fun loadPosts(numberPost: Int, loadMore: Boolean?) {
        if (lastVisible == null || !loadMore!!) {
            postList.clear()
            Log.d(ContentValues.TAG, "First load")
            mFirestore.collection("posts")
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
                                    Log.d(ContentValues.TAG, document.id + " => " + document.data)
                                    val post = document.toObject<Post>()
                                    post.pid = document.id
                                    if (!postList.contains(post)) {
                                        postList.add(post)
                                        posts.value = postList
                                    }
                                }
                            }
                        }else {
                            Log.d(ContentValues.TAG, "Error getting documents: ", task.exception)
                        }
                }
        } else {
            mFirestore.collection("posts")
                .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(numberPost.toLong())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let{ results ->
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
                                    results.documents[task.result!!.size() - 1]
                            } else {Log.d(ContentValues.TAG, "End of posts")}
                        }
                    } else {
                        Log.d(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
        }
    }

    fun searchPost(text: String){
        // TODO ("After using firebase function")
    }
}