package com.unlone.app.ui.lounge.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.*
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.unlone.app.R
import com.unlone.app.databinding.ActivityPostDetailBinding
import com.unlone.app.databinding.LayoutPostBinding
import com.unlone.app.utils.convertTimeStamp
import com.unlone.app.utils.dpConvertPx
import com.unlone.app.utils.getImageHorizontalMargin
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import java.util.*
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat

import android.text.style.ForegroundColorSpan

import android.widget.TextView
import com.unlone.app.instance.*
import com.unlone.app.ui.lounge.category.CategoriesViewModel


class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDetailBinding
    private lateinit var mergeLayoutPostBinding: LayoutPostBinding

    // declare viewModel
    private lateinit var detailedPostViewModel: DetailedPostViewModel
    private val commentViewModel: CommentViewModel by lazy {
        ViewModelProvider(this).get(CommentViewModel::class.java)
    }

    // init firebase
    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // detail of user and the post
    var uid: String? = mAuth.uid
    var username: String? = null
    private lateinit var pid: String

    var hashMap: HashMap<String, String>  // tag storing the save state
            = HashMap<String, String>()

    private var post: Post? = null
    private var comment: Comment? = null
    private val mComments: Long = 4   // how many comment loaded each time

    private val commentsAdapter by lazy{
        CommentsAdapter(
            pid,
            ::likeComment,
            ::likeSubComment,
            ::focusEdittextToSubComment
        )
    }

    // sub comment
    private var parentCid: String? = null
    private var parentCommenter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get id of the post using intent
        val intent = intent
        pid = intent.getStringExtra("postId").toString()

        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        val view = binding.root
        mergeLayoutPostBinding = LayoutPostBinding.bind(view)
        setContentView(view)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        // init toolbar
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionSave -> {

                    // User chose the "Saving" item, save the post...
                    if (hashMap["saveButton"] == "save") {
                        val timestamp =
                            hashMapOf("saveTime" to System.currentTimeMillis().toString())
                        mAuth.uid?.let { uid ->
                            mFirestore.collection("users").document(uid)
                                .collection("saved")
                                .document(pid)
                                .set(timestamp)
                                .addOnSuccessListener {
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot successfully written!"
                                    )
                                }
                                .addOnFailureListener { e -> Log.w(TAG, "Error saving post\n", e) }
                        }
                    } else {
                        mAuth.uid?.let { uid ->
                            mFirestore.collection("users").document(uid)
                                .collection("saved")
                                .document(pid)
                                .delete()
                                .addOnSuccessListener {
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot successfully deleted!"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        TAG,
                                        "Error deleting document",
                                        e
                                    )
                                }
                        }
                    }
                    true
                }

                R.id.actionReport -> {
                    // write the pid into the Report collection in Firestore
                    post?.let {

                        val reportMap = mapOf(
                            getString(R.string.hate_speech) to "Hate Speech",
                            getString(R.string.span_or_irrelevant) to "Span or Irrelevant",
                            getString(R.string.sexual_or_inappropriate) to "Sexual or Inappropriate",
                            getString(R.string.just_dont_like) to "I just don’t like it"
                        )
                        val singleItems = reportMap.keys.toList().toTypedArray()
                        var checkedItem = 1

                        // show dialog
                        MaterialAlertDialogBuilder(
                            this,
                            R.style.ThemeOverlay_App_MaterialAlertDialog
                        )
                            .setTitle(getString(R.string.why_report))
                            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                                // Respond to neutral button press
                            }
                            .setPositiveButton(getString(R.string.report)) { dialog, which ->
                                // Respond to positive button press
                                Log.d("TAG", singleItems[checkedItem])
                                val report = uid?.let { it1 ->
                                    Report.PostReport(
                                        post = post,
                                        reportReason = reportMap[singleItems[checkedItem]],
                                        reportedBy = it1
                                    )
                                }

                                Log.d("TAG", report.toString())
                                if (report != null) {
                                    mFirestore.collection("reports")
                                        .add(report)
                                        .addOnSuccessListener {
                                            Log.d(
                                                TAG,
                                                "Report DocumentSnapshot successfully written!"
                                            )
                                            showConfirmation()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                TAG,
                                                "Error saving post\n",
                                                e
                                            )
                                        }
                                }

                            }// Single-choice items (initialized with checked item)
                            .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                                // Respond to item chosen
                                Log.d("TAG", which.toString())
                                checkedItem = which

                            }
                            .show()
                    }
                    true
                }
                R.id.actionDelete -> {
                    // delete the post
                    MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.delete_alert))
                        .setMessage(getString(R.string.report_alert_context))
                        .setPositiveButton(getString(R.string.action_delete)) { dialog, which ->
                            // Respond to positive button press
                            detailedPostViewModel.deletePost(pid)
                            finish()
                        }
                        .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                            // Respond to positive button press
                        }
                        .show()
                    true
                }

                else -> false
            }
        }


        // load info
        detailedPostViewModel = ViewModelProvider(this).get(DetailedPostViewModel::class.java)
        detailedPostViewModel.loadPost(pid)
        loadPostInfo(binding)
        val saveButton = binding.topAppBar.menu.findItem(R.id.actionSave)
        isSaved(pid, saveButton)


        val layoutManager = LinearLayoutManager(this)
        binding.recycleview.layoutManager = layoutManager
        binding.recycleview.adapter = commentsAdapter

        // load comment
        commentViewModel.loadComments(mComments, pid)
        commentViewModel.comments.observe(
            this, {
                    it?.let {
                        Log.d("TAG", "comments in post detail activity: $it")
                        // commentsAdapter.submitList(null)
                        commentsAdapter.submitList(it)
                        commentsAdapter.notifyDataSetChanged()
                    }
                })

                // init load_more_comment button
                binding.moreCommentButton.setOnClickListener {
                    commentViewModel.loadComments(mComments, pid, true)
                    if (commentViewModel.endOfComments) {
                        binding.moreCommentButton.visibility = View.INVISIBLE
                    }
                }

                // send comment button click
                binding.sendBtn.setOnClickListener {
                    postComment()
                    binding.commentEt.text.clear()
                    val imm: InputMethodManager? =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                    binding.moreCommentButton.visibility = View.VISIBLE
                }
                binding.commentEt.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (parentCid != null && parentCommenter.toString() !in binding.commentEt.text) {
                            // replying prefix is destroyed
                            parentCid = null
                            parentCommenter = null
                            // remove the prefix directly
                            val arr = binding.commentEt.text.split(" ").toTypedArray()
                            val trimmedContent =
                                arr.filterNot { it == arr[0] }     // the content with the "@user" prefix"
                            if (trimmedContent.isEmpty()) {
                                binding.commentEt.setText("")
                            } else {
                                binding.commentEt.setText(trimmedContent[0])
                            }
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })
            }

                    private fun showConfirmation() {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.thank_you))
                    .setMessage(getString(R.string.report_text))
                    .setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                        // Respond to positive button press
                        finish()
                    }
                    .show()
            }

                    private fun loadPostInfo(binding: ActivityPostDetailBinding) {
                detailedPostViewModel.observablePost.observe(this, { p ->
                    p?.let {
                        post = p
                        // control the comment layout display (e.g. whether they have like button)
                        commentsAdapter.selfPost = (p.author_uid == mAuth.uid)

                        // enable or disable the right of delete post
                        val deleteMenuItem = binding.topAppBar.menu.findItem(R.id.actionDelete)
                        deleteMenuItem.isVisible = p.author_uid == mAuth.uid

                        // enable or disable save button
                        val saveButton = binding.topAppBar.menu.findItem(R.id.actionSave)
                        if (!p.save) {
                            saveButton.isEnabled = false
                            saveButton.icon.mutate().alpha = 135
                        }

                        // display title
                        mergeLayoutPostBinding.textViewTitle.text = p.title

                        // display image
                        val imagePath = p.imagePath
                        try {
                            // load image and resize it
                            // action wil be done when loading the image
                            val target: Target = object : Target {
                                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                                    mergeLayoutPostBinding.imageCover.visibility = View.VISIBLE

                                    //get measured image size
                                    val imageWidth = bitmap.width
                                    val imageHeight = bitmap.height
                                    mergeLayoutPostBinding.imageCover.setImageBitmap(bitmap)
                                    Log.d(
                                        "Bitmap Dimensions: ",
                                        imageWidth.toString() + "x" + imageHeight
                                    )
                                    val params = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    params.gravity = Gravity.CENTER
                                    val imageHorizontalMargin: Int = getImageHorizontalMargin(
                                        imageWidth.toFloat() / imageHeight,
                                        this@PostDetailActivity
                                    ) // in px
                                    val imageVerticalMargin =
                                        dpConvertPx(38, this@PostDetailActivity)
                                    params.setMargins(
                                        imageHorizontalMargin,
                                        imageVerticalMargin,
                                        imageHorizontalMargin,
                                        imageVerticalMargin
                                    )
                                    mergeLayoutPostBinding.imageCover.layoutParams = params

                                }

                                override fun onBitmapFailed(
                                    e: java.lang.Exception,
                                    errorDrawable: Drawable
                                ) {
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                            }

                            mergeLayoutPostBinding.imageCover.tag = target
                            Picasso.get().load(imagePath).into(target)
                        } catch (e: Exception) {
                            mergeLayoutPostBinding.imageCover.visibility = View.GONE
                        }

                        // display date
                        mergeLayoutPostBinding.date.text =
                            convertTimeStamp(p.createdTimestamp, Locale.getDefault().language)

                        // display topic
                        val rawTopic = p.category
                        val categoryViewModel: CategoriesViewModel =
                            ViewModelProvider(this).get(CategoriesViewModel::class.java)
                        categoryViewModel.getCategoryTitle(rawTopic)
                        categoryViewModel.categoryTitle.observe(this) { title ->
                            mergeLayoutPostBinding.topicTv.text = title
                        }

                        // display journal text
                        mergeLayoutPostBinding.textViewJournal.text = p.journal


                        // display label
                        var displayLabel = ""
                        for (label in p.labels) {
                            displayLabel += "· "
                            displayLabel += "$label "
                            val labelTv = TextView(this)
                            labelTv.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            labelTv.typeface =
                                ResourcesCompat.getFont(this, R.font.sf_pro_text_semibold)
                            labelTv.setTextColor(ContextCompat.getColor(this, R.color.colorText))
                            labelTv.letterSpacing = 0.01F
                            labelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                            labelTv.text = displayLabel
                            mergeLayoutPostBinding.labelGroup.addView(labelTv)
                        }
                        // if author doesn't allow commenting, will disappear the comment block
                        if (!p.comment) binding.commentLayout.visibility = View.GONE
                    }
                })
            }


                    private fun postComment() {
                //get data from comment edit text
                val content = binding.commentEt.text.toString().trim { it <= ' ' }
                // validate
                if (TextUtils.isEmpty(content)) {
                    // no value is entered
                    Toast.makeText(this, "Comment is empty", Toast.LENGTH_SHORT).show()
                    return
                }
                if (parentCid == null) {
                    // normal comment
                    commentViewModel.uploadComment(content, pid)
                } else {
                    // sub comment
                    commentViewModel.uploadSubComment(content, pid, parentCid!!)
                    // clear parent cid
                    parentCid = null
                }
                commentViewModel.loadComments(mComments, pid)
            }


                    private fun isSaved(pid: String, saveButton: MenuItem) {
                mFirestore.collection("users").document(mAuth.uid!!)
                    .collection("saved")
                    .document(pid)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: ${snapshot.data}")
                            saveButton.icon =
                                ContextCompat.getDrawable(this, R.drawable.ic_baseline_bookmark_24)
                            hashMap["saveButton"] = "saved"
                        } else {
                            if (snapshot != null) {
                                saveButton.icon = ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_baseline_bookmark_border_24
                                )
                                hashMap["saveButton"] = "save"
                            }
                        }
                    }
            }

                    private fun likeComment(comment: Comment) {
                commentViewModel.processCommentLike(comment, pid)
                commentViewModel.loadComments(mComments, pid, true)
            }

                    private fun likeSubComment(subComment: SubComment) {
                commentViewModel.processSubCommentLike(subComment, pid)
                commentViewModel.loadComments(mComments, pid, true)
            }

                    private fun focusEdittextToSubComment(cid: String, username: String) {
                parentCid = cid
                parentCommenter = username
                val editText = binding.commentEt

                // add prefix
                val prefix = "@$username "
                val prefixToSpan: Spannable = SpannableString(prefix)
                prefixToSpan.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.labelled)),
                    0,
                    prefix.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                editText.setText(prefixToSpan, TextView.BufferType.EDITABLE)

                // set focus and open the soft keyboard
                editText.requestFocus()
                val imm: InputMethodManager? =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

                // move the cursor to the end
                editText.setSelection(editText.length())
            }


                    companion object {
                private const val TAG = "PostDetailedActivity"
            }

    }
