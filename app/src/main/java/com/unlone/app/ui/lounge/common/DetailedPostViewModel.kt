package com.unlone.app.ui.lounge.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.unlone.app.instance.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.ktx.toObject
import java.util.ArrayList

class DetailedPostViewModel : ViewModel() {
    private val post: MutableLiveData<Post?> = MutableLiveData()
    private val postList: List<Post>
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mFirestore: FirebaseFirestore
    private val uid: String? = mAuth.uid
    val observablePost: LiveData<Post?>
        get() = post

    fun loadPost(pid: String) {
        mFirestore.collection("posts")
            .document(pid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val p = documentSnapshot.toObject<Post>()
                post.value = p
                Log.d("TAG", "detailedPost: ${post.value.toString()}")
                Log.d("TAG", "pid: $pid")
            }
    }

    fun deletePost(pid:String){
        mFirestore.collection("posts")
            .document(pid)
            .delete()
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }

    }


    init {
        postList = ArrayList()
        mFirestore = FirebaseFirestore.getInstance()
    }
}