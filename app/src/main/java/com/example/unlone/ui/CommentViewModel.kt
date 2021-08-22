package com.example.unlone.ui

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unlone.instance.Comment
import com.example.unlone.instance.Post
import com.example.unlone.ui.Create.PostData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import java.util.*

class CommentViewModel: ViewModel() {
    private var _comments: MutableLiveData<List<Comment>> = MutableLiveData()
    private var _commentList: MutableList<Comment> = mutableListOf()
    //private var mAuth: FirebaseAuth? = null
    private var mFirestore = FirebaseFirestore.getInstance()
    val comments: LiveData<List<Comment>> = _comments

    fun loadComments(numberPost: Int, pid: String) {
        _commentList.clear()
        mFirestore.collection("posts").document(pid)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)        // will be ranked by AI later
                .limit(numberPost.toLong())
                .get()
                .addOnSuccessListener  { result  ->
                    Log.d("TAG", "number of comments: "+result.size())
                    for (document in result) {
                        Log.d(TAG, document.id + " ==> " + document.data)
                        val comment = document.toObject<Comment>()
                        Log.d("TAG", "comment object: $comment")
                        if (!_commentList.contains(comment)) {
                            _commentList.add(comment)
                            _comments.value = _commentList
                            Log.d("TAG", "commentsss: "+ _comments.value)
                        }
                    }
                    Log.d("TAG", ""+result)

                }.addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
    }
}