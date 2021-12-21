package com.unlone.app.ui.lounge.common

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.unlone.app.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.ArrayList

class DetailedPostViewModel : ViewModel() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()
    val uid = mAuth.uid.toString()

    // post field
    private val post: MutableLiveData<Post?> = MutableLiveData()
    val observablePost: LiveData<Post?>
        get() = post

    // comments list field
    private val mComments: Long = 4   // how many comment loaded each time
    private var _comments: MutableLiveData<List<Comment>> = MutableLiveData()
    private var _commentList: MutableList<Comment> = mutableListOf()

    private var _uiComments: MutableLiveData<List<UiComment>> = MutableLiveData()
    val uiComments: LiveData<List<UiComment>> = _uiComments

    private var lastVisible: Float? = null
    var endOfComments: Boolean = false

    // type comment edittext field
    private var _commentEditTextFocused: MutableLiveData<Boolean> = MutableLiveData()
    val commentEditTextFocused: LiveData<Boolean> = _commentEditTextFocused
    var parentCid: String? = null
    var parentCommenter: String? = null

    fun focusEdittextToSubComment(parentCid: String, parentCommenter: String) {
        _commentEditTextFocused.value = true
        this.parentCid = parentCid
        this.parentCommenter = parentCommenter
    }

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

    fun deletePost(pid: String) {

        mFirestore.collection("posts")
            .document(pid)
            .delete()
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
    }

    fun savePost(pid: String, timestamp: HashMap<String, String>) {
        mAuth.uid?.let { uid ->
            mFirestore.collection("users").document(uid)
                .collection("saved")
                .document(pid)
                .set(timestamp)
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }
    }

    fun unsavePost(pid: String) {
        mAuth.uid?.let { uid ->
            mFirestore.collection("users").document(uid)
                .collection("saved")
                .document(pid)
                .delete()
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }
    }

    fun uploadReport(report: Report) {
        mFirestore.collection("reports")
            .add(report)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    suspend fun isSaved(pid: String): Boolean {
        val result = mFirestore.collection("users").document(mAuth.uid!!)
            .collection("saved")
            .document(pid)
            .get()
            .await()
        return result != null && result.exists()

    }

    fun loadUiComments(pid: String, loadMore: Boolean = false, numberPost: Long = mComments) {
        val uiCommentList: ArrayList<UiComment> = ArrayList()
        viewModelScope.launch(Dispatchers.IO) {
            loadComments(pid, loadMore, numberPost)

            withContext(Dispatchers.Main) {
                for (comment in _comments.value!!) {
                    val isLiked = fireStoreIsLike(comment)
                    // load sub comments
                    val uiSubComments = loadUiSubComments(pid, comment, numberPost)
                    uiCommentList.add(UiComment(comment, isLiked, uiSubComments))
                }
                _uiComments.value = uiCommentList.distinct()
            }
        }

    }

    private suspend fun loadComments(
        pid: String,
        loadMore: Boolean,
        numberPost: Long
    ) {

        if (lastVisible == null || !loadMore) {
            _commentList.clear()
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
                        _commentList.add(comment)
                        lastVisible = comment.score
                    }
                }
                // sort the postList
                val sortedCommentList = _commentList.sortedByDescending { it.score }
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "sorted postList: $sortedCommentList")
                    _comments.value = sortedCommentList
                }

            }

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
                        if (comment.referringPid == null) {
                            comment.referringPid = pid
                        }

                        lastVisible = comment.score
                        Log.d("TAG", "comment object LOADED: $comment")
                        _commentList.add(comment)
                    }
                }
                // sort the postList
                val sortedCommentList = _commentList.sortedByDescending { it.score }
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "sorted postList: $sortedCommentList")
                    _comments.value = sortedCommentList
                }
            }
        }
    }

    private suspend fun loadUiSubComments(
        pid: String,
        comment: Comment,
        numberPost: Long = mComments
    ): ArrayList<UiSubComment> {
        val uiSubCommentList = ArrayList<UiSubComment>()
        val subCommentList = loadSubComments(pid, comment, numberPost)
        for (sc in subCommentList) {
            val isLiked = fireStoreSubCommentIsLike(sc)
            uiSubCommentList.add(UiSubComment(sc, isLiked))
        }
        return uiSubCommentList
    }

    private suspend fun loadSubComments(
        pid: String,
        comment: Comment,
        numberPost: Long = mComments
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


    private suspend fun fireStoreIsLike(comment: Comment): Boolean {
        val result = comment.referringPid?.let {
            mFirestore.collection("posts").document(it)
                .collection("comments").document(comment.cid!!)
                .collection("likes").whereEqualTo("likedBy", mAuth.uid)
                .get()
                .await()
        }

        val likeList = ArrayList<String>()
        for (doc in result!!) {
            doc.getString("likedBy")?.let {
                likeList.add(it)
            }
        }
        assert(likeList.size <= 1)
        Log.d(ContentValues.TAG, "People who has liked: $likeList")

        return likeList.size == 1       // this user has like the comment

    }

    private suspend fun fireStoreSubCommentIsLike(subComment: SubComment): Boolean {
        val result = subComment.parent_pid?.let {
            mFirestore.collection("posts").document(it)
                .collection("comments").document(subComment.parent_cid!!)
                .collection("sub comments").document(subComment.cid!!)
                .collection("likes").whereEqualTo("likedBy", mAuth.uid)
                .get()
                .await()
        }

        val likeList = ArrayList<String>()
        for (doc in result!!) {
            doc.getString("likedBy")?.let {
                likeList.add(it)
            }
        }
        assert(likeList.size <= 1)
        Log.d(ContentValues.TAG, "People who has liked: $likeList")

        return likeList.size == 1       // this user has like the comment

    }

    fun processCommentLike(uiComment: UiComment) {
        val comment = uiComment.comment
        Log.d("TAG", "comment: $comment")

        val docRef = comment.referringPid?.let {
            mFirestore.collection("posts").document(it)
                .collection("comments").document(comment.cid!!)
                .collection("likes")
        }

        viewModelScope.launch(Dispatchers.IO) {
            val result = docRef?.get()?.await()
            val removeDocId = async {
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

            if (removeDocId.await() != null) {
                // Remove the like document
                docRef?.document(removeDocId.await() as String)?.delete()?.await()
                // update the UiComment list
                // this comment is now UNLIKED by the user
                _uiComments.value?.find { it.comment.cid == comment.cid }?.likedByUser = false
            } else {
                // Add the like document
                val data = hashMapOf(
                    "likedBy" to mAuth.uid,
                    "timeStamp" to (System.currentTimeMillis() / 1000).toString()
                )
                val docRefId = docRef?.add(data)?.await()?.id
                // update the UiComment list
                // this comment is now LIKED by the user
                _uiComments.value?.find { it.comment.cid == comment.cid }?.likedByUser = true

            }
        }
    }

    fun processSubCommentLike(subComment: SubComment) {
        Log.d("TAG", "subComment: $subComment")

        val docRef = subComment.cid?.let {commentCid ->
            subComment.parent_cid?.let { parentCid ->
                subComment.parent_pid?.let { ParentPid ->
                    mFirestore.collection("posts").document(ParentPid)
                        .collection("comments").document(parentCid)
                        .collection("sub comments").document(commentCid)
                        .collection("likes")
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
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

    fun uploadComment(commentContent: String, pid: String) {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun uploadSubComment(commentContent: String, pid: String, cid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val subComment = SubComment(
                uid = mAuth.uid!!,
                content = commentContent,
                timestamp = System.currentTimeMillis().toString(),
                parent_cid = cid,
                parent_pid = pid
            )
            mFirestore.collection("posts").document(pid)
                .collection("comments").document(cid)
                .collection("sub comments")
                .add(subComment)
                .await()
        }
    }


    fun clearSubCommentPrerequisite() {
        parentCid = null
        parentCommenter = null
    }
}