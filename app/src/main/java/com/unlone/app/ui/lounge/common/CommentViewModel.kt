package com.unlone.app.ui.lounge.common

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.squareup.okhttp.Dispatcher
import com.unlone.app.instance.Comment
import com.unlone.app.instance.SubComment
import com.unlone.app.instance.User
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class CommentViewModel : ViewModel() {
    private var _comments: MutableLiveData<List<Comment>> = MutableLiveData()
    private var _commentList: MutableList<Comment> = mutableListOf()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mFirestore = FirebaseFirestore.getInstance()
    val comments: LiveData<List<Comment>> = _comments

    // private var lastVisible: DocumentSnapshot? = null
    private var lastVisible: Float? = null
    var endOfComments: Boolean = false


    fun loadComments(numberPost: Long, pid: String, loadMore: Boolean = false) {

        if (lastVisible == null || !loadMore) {
            _commentList.clear()
            viewModelScope.launch(Dispatchers.IO) {

                val commentCollection =
                    mFirestore.collection("posts").document(pid).collection("comments")
                        .orderBy("score", Query.Direction.DESCENDING)
                        .limit(numberPost)
                        .get()
                        .await()

                endOfComments = commentCollection.size() < numberPost

                Log.d("TAG", "number of comments: " + commentCollection.size())
                for (document in commentCollection) {
                    Log.d(TAG, document.id + " ==> " + document.data)
                    var comment = document.toObject<Comment>()
                    comment.cid = document.id
                    val userDoc = mFirestore.collection("users")
                        .document(comment.uid!!)
                        .get()
                        .await()
                    val user = userDoc.toObject(User::class.java)
                    withContext(Dispatchers.Main) {
                        comment.username = user?.username

                        // load sub comments
                        comment = addSubComments(
                            numberPost,
                            pid,
                            comment
                        )
                        Log.d("TAG", "comment object: $comment")
                        _commentList.add(comment)
                        lastVisible = comment.score
                    }
                }
                withContext(Dispatchers.Main){
                    _comments.value = _commentList
                    Log.d("TAG", "commentsss: " + _comments.value)
                }
            }

        } else {
            // run when order more comments
            Log.d("TAG", "lastVisible: $lastVisible")
            viewModelScope.launch(Dispatchers.IO) {
                val commentCollection =
                    mFirestore.collection("posts").document(pid).collection("comments")
                        .orderBy("score", Query.Direction.DESCENDING)
                        .startAfter(lastVisible)
                        .limit(numberPost)
                        .get()
                        .await()

                endOfComments = commentCollection.size() < numberPost

                Log.d("TAG", "number of NEXT comments: " + commentCollection.size())
                for (document in commentCollection) {
                    Log.d(TAG, document.id + " ==> " + document.data)
                    var comment = document.toObject<Comment>()
                    comment.cid = document.id
                    // check if comment already existed
                    fun List<Comment>.filterByCid(cid: String) = this.filter { it.cid == cid }
                    val containedComments = _commentList.filterByCid(document.id)
                    if (containedComments.isNotEmpty()) {
                        // comment already existed
                            Log.d("TAG", "caught duplicated comment: $comment")
                        continue
                    }
                    val userDoc = mFirestore.collection("users")
                        .document(comment.uid!!)
                        .get()
                        .await()
                    val user = userDoc.toObject(User::class.java)
                    withContext(Dispatchers.Main) {
                        comment.username = user?.username

                        // load sub comments
                        comment = addSubComments(
                            numberPost,
                            pid,
                            comment
                        )
                        lastVisible = comment.score
                        Log.d("TAG", "comment object LOADED: $comment")
                        _commentList.add(comment)
                    }
                }
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "comments: " + _comments.value)
                    _comments.value = _commentList
                }
            }
        }
    }

    private suspend fun addSubComments(numberPost: Long, pid: String, comment: Comment): Comment {
        val subCommentList: MutableList<SubComment> = mutableListOf()
        val subCommentCollection = comment.cid?.let {
            mFirestore.collection("posts").document(pid)
                .collection("comments").document(it)
                .collection("sub comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
        }

        withContext(Dispatchers.Main) {
            if (subCommentCollection != null) {
                for (sc in subCommentCollection) {
                    Log.d(TAG, sc.id + " => " + sc.data)
                    val subComment = sc.toObject<SubComment>()
                    subComment.cid = sc.id
                    withContext(Dispatchers.IO) {
                        val userDoc = mFirestore.collection("users")
                            .document(subComment.uid!!)
                            .get()
                            .await()
                        val user = userDoc.toObject(User::class.java)
                        withContext(Dispatchers.Main) {
                            subComment.username = user?.username
                        }
                    }
                    subCommentList.add(subComment)
                }
                comment.subComments = subCommentList
                Log.d(TAG, "comment with sub comments added: $comment")
            }
        }
        return comment
    }

    fun processCommentLike(comment: Comment, pid: String) {
        Log.d("TAG", "comment: $comment")

        val docRef = mFirestore.collection("posts").document(pid)
            .collection("comments").document(comment.cid!!)
            .collection("likes")

        CoroutineScope(Dispatchers.IO).launch {
            val result = docRef.get().await()
            val removeDocId = async {
                // Find if the user has already like the comment, unlike it if found
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    if (document.getString("likedBy") == mAuth.uid) {
                        // Found the document, remove later
                        return@async document.id
                    }
                }
                return@async null
            }

            if (removeDocId.await() != null) {
                // Remove the like document
                docRef.document(removeDocId.await() as String)
                    .delete().await()
            } else {
                // add the like document
                val data = hashMapOf(
                    "likedBy" to mAuth.uid,
                    "timeStamp" to (System.currentTimeMillis() / 1000).toString()
                )
                val docRefId = docRef.add(data).await().id
            }
        }
    }


    fun processSubCommentLike(subComment: SubComment, pid: String) {
        Log.d("TAG", "subComment: $subComment")

        val docRef = subComment.cid?.let {
            subComment.parent_cid?.let { it1 ->
                mFirestore.collection("posts").document(pid)
                    .collection("comments").document(it1)
                    .collection("sub comments").document(it)
                    .collection("likes")
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val result = docRef?.get()?.await()
            val removeDocId = async {
                // Find if the user has already like the comment, unlike it if found
                if (result != null) {
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        if (document.getString("likedBy") == mAuth.uid) {
                            // Found the document, remove later
                            return@async document.id
                        }
                    }
                }
                return@async null
            }

            if (removeDocId.await() != null) {
                // Remove the like document
                docRef?.document(removeDocId.await() as String)?.delete()?.await()
            } else {
                // add the like document
                val data = hashMapOf(
                    "likedBy" to mAuth.uid,
                    "timeStamp" to (System.currentTimeMillis() / 1000).toString()
                )
                val docRefId = docRef?.add(data)?.await()?.id
            }
        }
    }

    fun uploadComment(commentContent: String, pid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val comment = Comment(
                uid = mAuth.uid!!,
                content = commentContent,
                timestamp = System.currentTimeMillis().toString()
            )
            mFirestore.collection("posts").document(pid)
                .collection("comments")
                .add(comment)
                .await()
        }
    }

    fun uploadSubComment(commentContent: String, pid: String, cid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val subComment = SubComment(
                uid = mAuth.uid!!,
                content = commentContent,
                timestamp = System.currentTimeMillis().toString(),
                parent_cid = cid
            )
            mFirestore.collection("posts").document(pid)
                .collection("comments").document(cid)
                .collection("sub comments")
                .add(subComment)
                .await()
        }
    }
}