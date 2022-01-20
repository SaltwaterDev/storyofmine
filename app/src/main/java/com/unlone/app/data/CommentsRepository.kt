package com.unlone.app.data

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.unlone.app.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentsRepository @Inject constructor() {
    private var lastVisible: Float? = null
    var endOfComments: Boolean = true
    private val mAuth = Firebase.auth
    private val mFirestore = Firebase.firestore

    suspend fun loadComments(
        pid: String,
        loadMore: Boolean,
        numberPost: Long
    ): List<Comment> {
        val commentList = ArrayList<Comment>()
        if (lastVisible == null || !loadMore) {
            withContext(Dispatchers.IO) {
                val commentCollection =
                    mFirestore.collection("posts").document(pid).collection("comments")
                        .orderBy("score", Query.Direction.DESCENDING)
                        .limit(numberPost)
                        .get()
                        .await()
                endOfComments = commentCollection.size() < numberPost

                Log.d("TAG", "number of comments: " + commentCollection.size())
                for (document in commentCollection) {
                    Log.d(ContentValues.TAG, document.id + " ==> " + document.data)
                    val comment = document.toObject<Comment>()
                    comment.cid = document.id
                    val userDoc = mFirestore.collection("users")
                        .document(comment.uid!!)
                        .get()
                        .await()
                    val user = userDoc.toObject(User::class.java)
                    withContext(Dispatchers.Main) {
                        comment.username = user?.username
                        if (comment.referringPid == null) {
                            comment.referringPid = pid
                        }

                        Log.d("TAG", "comment object: $comment")
                        commentList.add(comment)
                        lastVisible = comment.score
                    }
                }
            }
            // sort the postList
            return commentList.sortedByDescending { it.score }.distinct()

        } else {
            // run when order more comments
            Log.d("TAG", "lastVisible: $lastVisible")
            withContext(Dispatchers.IO) {
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
                    Log.d(ContentValues.TAG, document.id + " ==> " + document.data)
                    val comment = document.toObject<Comment>()
                    comment.cid = document.id

                    // check if comment already existed
                    fun List<Comment>.filterByCid(cid: String) = this.filter { it.cid == cid }
                    val containedComments = commentList.filterByCid(document.id)
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
                        if (comment.referringPid == null) {
                            comment.referringPid = pid
                        }
                        lastVisible = comment.score
                        Log.d("TAG", "comment object LOADED: $comment")
                        commentList.add(comment)
                    }
                }

            }
        }
        // sort the postList
        return commentList.sortedByDescending { it.score }.distinct()
    }

    suspend fun loadSubComments(
        pid: String,
        comment: Comment,
        numberPost: Long
    ): MutableList<SubComment> {
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
                    Log.d(ContentValues.TAG, sc.id + " => " + sc.data)
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
                            if (subComment.parent_pid == null) {
                                subComment.parent_pid = pid
                            }
                        }
                    }
                    subCommentList.add(subComment)
                }
                subCommentList.sortedByDescending { it.timestamp }
                Log.d(ContentValues.TAG, "comment with sub comments added: $comment")
            }
        }

        return subCommentList
    }


    suspend fun fireStoreIsLike(comment: Comment): Boolean {
        val result = comment.referringPid?.let {
            mFirestore.collection("posts").document(it)
                .collection("comments").document(comment.cid!!)
                .collection("likes").whereEqualTo("likedBy", mAuth.uid)
                .get()
                .await()
        }

        val likeList = java.util.ArrayList<String>()
        for (doc in result!!) {
            doc.getString("likedBy")?.let {
                likeList.add(it)
            }
        }
        assert(likeList.size <= 1)
        Log.d(ContentValues.TAG, "People who has liked: $likeList")

        return likeList.size == 1       // this user has like the comment

    }

    suspend fun fireStoreSubCommentIsLike(subComment: SubComment): Boolean {
        val result = subComment.parent_pid?.let {
            mFirestore.collection("posts").document(it)
                .collection("comments").document(subComment.parent_cid!!)
                .collection("sub comments").document(subComment.cid!!)
                .collection("likes").whereEqualTo("likedBy", mAuth.uid)
                .get()
                .await()
        }

        val likeList = java.util.ArrayList<String>()
        for (doc in result!!) {
            doc.getString("likedBy")?.let {
                likeList.add(it)
            }
        }
        assert(likeList.size <= 1)
        Log.d(ContentValues.TAG, "People who has liked: $likeList")

        return likeList.size == 1       // this user has like the comment

    }

    suspend fun processCommentLike(uiComment: UiComment): Boolean {
        val comment = uiComment.comment
        Log.d("TAG", "comment: $comment")

        val docRef = comment.referringPid?.let {
            mFirestore.collection("posts").document(it)
                .collection("comments").document(comment.cid!!)
                .collection("likes")
        }

        val removeDocId = withContext(Dispatchers.IO) {
            val result = docRef?.get()?.await()
            async {
                // Find if the user has already like the comment, unlike it if found
                if (result != null) {
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        if (document.getString("likedBy") == mAuth.uid) {
                            return@async document.id
                        }
                    }
                }
                return@async null
            }
        }
        return (removeDocId.await() == null)
    }

    suspend fun processSubCommentLike(subComment: SubComment) {
        Log.d("TAG", "subComment: $subComment")

        val docRef = subComment.cid?.let { commentCid ->
            subComment.parent_cid?.let { parentCid ->
                subComment.parent_pid?.let { ParentPid ->
                    mFirestore.collection("posts").document(ParentPid)
                        .collection("comments").document(parentCid)
                        .collection("sub comments").document(commentCid)
                        .collection("likes")
                }
            }
        }

        withContext(Dispatchers.IO) {
            val result = docRef?.get()?.await()
            val removeDocId = async {
                // Find if the user has already like the comment, unlike it if found
                if (result != null) {
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
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
                Log.d("TAG", "like doc added: $docRefId")
            }
        }
    }


    suspend fun uploadComment(pid: String, commentContent: String) {
        withContext(Dispatchers.IO) {
            val comment = Comment(
                uid = mAuth.uid!!,
                content = commentContent,
                timestamp = System.currentTimeMillis().toString(),
                referringPid = pid
            )
            mFirestore.collection("posts").document(pid)
                .collection("comments")
                .add(comment)
                .await()
        }
    }


    suspend fun uploadSubComment(pid: String, commentContent: String, parentCid: String) {
        withContext(Dispatchers.IO) {
            val subComment = SubComment(
                uid = mAuth.uid!!,
                content = commentContent,
                timestamp = System.currentTimeMillis().toString(),
                parent_cid = parentCid,
                parent_pid = pid
            )
            parentCid.let {
                mFirestore.collection("posts").document(pid)
                    .collection("comments").document(it)
                    .collection("sub comments")
                    .add(subComment)
                    .await()
            }
        }
    }

    fun uploadReport(report: Report) {
        mFirestore.collection("reports")
            .add(report)
    }

}