package com.unlone.app.ui.lounge.common

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.unlone.app.R
import com.unlone.app.data.CategoriesRepository
import com.unlone.app.data.CommentsRepository
import com.unlone.app.data.PostsRepository
import com.unlone.app.model.*
import com.unlone.app.utils.ObservableViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DetailedPostViewModel(val pid: String) : ObservableViewModel() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val uid = mAuth.uid.toString()

    private val categoriesRepository = CategoriesRepository()
    private val postsRepository = PostsRepository()
    private val commentsRepository = CommentsRepository()

    // post field
    private val post: MutableLiveData<Post?> = MutableLiveData()
    val observablePost: LiveData<Post?>
        get() = post
    private val _category: MutableLiveData<String?> = MutableLiveData()
    val category: LiveData<String?> = _category
    private val _isPostSaved: MutableLiveData<Boolean> = MutableLiveData(false)
    val isPostSaved: LiveData<Boolean> = _isPostSaved


    // comments list field
    private val mComments: Long = 4   // how many comment loaded each time
    private var _comments: MutableLiveData<List<Comment>> = MutableLiveData()

    private var _uiComments: MutableLiveData<List<UiComment>> = MutableLiveData()
    val uiComments: LiveData<List<UiComment>> = _uiComments

    var endOfComments: Boolean = true

    // type comment edittext field
    private var _commentEditTextFocused: MutableLiveData<Boolean> = MutableLiveData(false)
    val commentEditTextFocused: LiveData<Boolean> = _commentEditTextFocused
    var parentCid: String? = null
    var parentCommenter: String? = null
    val isSelfPost by lazy { post.value?.author_uid == uid }

    // Report
    private val reportMap = mapOf(
        (R.string.hate_speech) to "Hate Speech",
        (R.string.span_or_irrelevant) to "Span or Irrelevant",
        (R.string.sexual_or_inappropriate) to "Sexual or Inappropriate",
        (R.string.just_dont_like) to "I just don’t like it"
    )
    val singleItems = reportMap.keys.toList().toTypedArray()

    init {
        viewModelScope.launch {
            post.value = loadPost()
            _category.value = getCategoryTitle()
            isSaved()
        }
    }

    private suspend fun loadPost() = postsRepository.loadPost(pid)

    fun deletePost() = viewModelScope.launch {
        postsRepository.deletePost(pid)
    }

    fun performSavePost() {
        if (_isPostSaved.value != true) {
            _isPostSaved.value = postsRepository.savePost(pid)
        } else {
            // User uncheck chose the "Saving" item, save the post...
            _isPostSaved.value = postsRepository.unsavePost(pid)
        }
    }

    fun reportPost(checkedItem: Int) {
        val report = Report.PostReport(
            post = post.value,
            reportReason = reportMap[singleItems[checkedItem]],
            reportedBy = uid
        )
        Log.d("TAG", report.toString())
        postsRepository.uploadReport(report)
    }

    fun reportComment(comment: Comment, checkedItem: Int) {
        val report = Report.CommentReport(
            comment = comment,
            reportReason = reportMap[singleItems[checkedItem]],
            reportedBy = uid
        )
        Log.d("TAG", report.toString())
        commentsRepository.uploadReport(report)
    }

    fun reportSubComment(subComment: SubComment, checkedItem: Int) {
        val report = Report.SubCommentReport(
            subComment = subComment,
            reportReason = reportMap[singleItems[checkedItem]],
            reportedBy = uid
        )
        Log.d("TAG", report.toString())
        commentsRepository.uploadReport(report)
    }

    private suspend fun isSaved() {
        postsRepository.isSaved(pid)
    }


    fun loadUiComments(loadMore: Boolean = false) {
        val uiCommentList: ArrayList<UiComment> = ArrayList()
        viewModelScope.launch(Dispatchers.IO) {
            val comments = commentsRepository.loadComments(pid, loadMore, mComments)
            withContext(Dispatchers.Main) {
                comments.let{ comments ->
                    for (comment in comments) {
                        val isLiked = commentsRepository.fireStoreIsLike(comment)
                        // load sub comments
                        val uiSubComments = loadUiSubComments(comment, mComments)
                        uiCommentList.add(UiComment(comment, isLiked, false, uiSubComments))
                    }
                    _uiComments.value = uiCommentList.distinct()
                }
            }
        }
    }

    private suspend fun loadUiSubComments(
        comment: Comment,
        numberPost: Long = mComments
    ): ArrayList<UiSubComment> {
        val uiSubCommentList = ArrayList<UiSubComment>()
        val subCommentList = commentsRepository.loadSubComments(pid, comment, numberPost)
        for (sc in subCommentList) {
            val isLiked = commentsRepository.fireStoreSubCommentIsLike(sc)
            uiSubCommentList.add(UiSubComment(sc, isLiked))
        }
        return uiSubCommentList
    }

    fun processCommentLike(uiComment: UiComment) {
        viewModelScope.launch {
            val comment = uiComment.comment
            _uiComments.value?.find { it.comment.cid == comment.cid }?.likedByUser =
                commentsRepository.processCommentLike(uiComment)
        }
    }

    fun processSubCommentLike(subComment: SubComment) {
        CoroutineScope(Dispatchers.IO).launch {
            commentsRepository.processSubCommentLike(subComment)
        }
    }

    fun uploadComment(commentContent: String) {
        viewModelScope.launch {
            // clear parent cid and username
            clearSubCommentPrerequisite()
            if (parentCid == null) {
                // normal comment
                commentsRepository.uploadComment(pid, commentContent)
            } else {
                // sub comment
                parentCid?.let { commentsRepository.uploadSubComment(pid, commentContent, it) }
            }
        }
    }

    fun focusEdittextToSubComment(parentCid: String, parentCommenter: String) {
        _commentEditTextFocused.value = true
        this.parentCid = parentCid
        this.parentCommenter = parentCommenter
    }

    fun clearSubCommentPrerequisite() {
        _commentEditTextFocused.value = false
        parentCid = null
        parentCommenter = null
    }

    // display topic
    private suspend fun getCategoryTitle(): String? {
        post.value?.category?.let{ categoryId ->
            return categoriesRepository.getCategoryTitle(categoryId)}
        return null
    }
}