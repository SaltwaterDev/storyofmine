package com.example.unlone.ui.profile

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
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class SavedPostsViewModel : ViewModel() {
    val posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val postList: MutableList<Post> = ArrayList()
    val mAuth = FirebaseAuth.getInstance()
    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var lastVisible: DocumentSnapshot? = null
    fun getPosts(): LiveData<List<Post>> {
        return posts
    }

    // retrieve the list of saved posts
    private suspend fun savedPostsReference(): List<DocumentSnapshot> {
        return mFirestore.collection("users").document(mAuth.uid!!)
            .collection("saved")
            .get()
            .await()
            .documents
    }

    fun loadPosts() { // this: CoroutineScope
        val savedPostList: ArrayList<String> = ArrayList()
        viewModelScope.launch (Dispatchers.IO){ // launch a new coroutine and continue
            val savedPostsReference = async { savedPostsReference() }
            for (document in savedPostsReference.await()) {
                savedPostList.add(document.id)
            }
            Log.d("TAG","savedPostList: $savedPostList") // main coroutine continues while a previous one is delayed

            for (pid in savedPostList) {
                val post = mFirestore.collection("posts")
                    .document(pid)
                    .get()
                    .await()
                    .toObject<Post>()

                if (post != null) {
                    post.pid = pid
                    withContext(Dispatchers.Main) {
                        if (!postList.contains(post)) {
                            postList.add(post)
                        }
                    }
                }
            }
            posts.postValue(postList)
            Log.d(ContentValues.TAG, "added posts: ${posts.value}")
            Log.d(ContentValues.TAG, "added postList: $postList")
        }
    }


}