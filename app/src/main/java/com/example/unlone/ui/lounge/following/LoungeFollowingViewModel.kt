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


    private suspend fun loadFollowingCategories(): ArrayList<String> {
        // retrieve the following categories first
        return mFirestore.collection("users")
            .document(mAuth.uid!!)
            .get()
            .await()
            .data
            ?.get("followingCategories") as ArrayList<String>
    }

    fun loadPosts(numberPost: Int, loadMore: Boolean?) {

        viewModelScope.launch(Dispatchers.IO) {
            // retrieve the following categories
            val followingCategories = async { loadFollowingCategories() }

            // load the post only with the following categories
            Log.d(ContentValues.TAG, "followingCategories: $followingCategories")
            if (lastVisible == null || !loadMore!!) {
                postList.clear()
                Log.d(ContentValues.TAG, "First load")
                mFirestore.collection("posts")
                    .whereIn("category", followingCategories.await())
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
                    .whereIn("category", followingCategories.await())
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


    }

    fun searchPost(text: String){
        // TODO ("After using firebase function")
    }
}