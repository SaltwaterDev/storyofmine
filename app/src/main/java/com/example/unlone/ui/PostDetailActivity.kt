package com.example.unlone.ui

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.example.unlone.instance.Post
import androidx.annotation.RequiresApi
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.example.unlone.databinding.ActivityPostDetailBinding
import com.example.unlone.instance.Comment
import com.example.unlone.instance.User
import com.google.firebase.firestore.EventListener
import java.lang.Exception

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDetailBinding

    private var detailedPostViewModel: DetailedPostViewModel? = null
    protected var mFirestore: FirebaseFirestore? = null
    private var mAuth: FirebaseAuth? = null
    private val post: Post? = null
    private var comment: Comment? = null

    // detail of user and the post
    var uid: String? = null
    var username: String? = null
    private lateinit var pid: String

    //add comment views
    var commentEt: EditText? = null
    var sendBtn: ImageButton? = null
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // init database storing
        mFirestore = FirebaseFirestore.getInstance()
        detailedPostViewModel = DetailedPostViewModel()
        mAuth = FirebaseAuth.getInstance()

        // get id of the post using intent
        val intent = intent
        pid = intent.getStringExtra("postId").toString()

        
        // load info
        detailedPostViewModel = ViewModelProvider(this).get(DetailedPostViewModel::class.java)
        detailedPostViewModel!!.loadPost(pid)
        loadPostInfo(binding)

        // send comment button click
        binding.sendBtn.setOnClickListener(View.OnClickListener {
            postComment()
            binding.commentEt.getText().clear()
        })
    }

    private fun loadPostInfo(binding: ActivityPostDetailBinding) {
        detailedPostViewModel!!.observablePost.observe(this, { p ->
            binding.textViewTitle.text = p!!.title

            // display image
            val imagePath = p.imagePath
            try {
                Picasso.get().load(imagePath).into(binding.imageCover)
                binding.imageCover.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.imageCover.visibility = View.GONE
            }

            // display journal text
            binding.textViewJournal.text = p.journal
            binding.date.text = p.createdDate

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun postComment() {

        //get data from comment edit text
        val comment_content = commentEt!!.text.toString().trim { it <= ' ' }
        // validate
        if (TextUtils.isEmpty(comment_content)) {
            // no value is entered
            Toast.makeText(this, "Comment is empty", Toast.LENGTH_SHORT).show()
            return
        }
        val docRef = mFirestore!!.collection("users").document(mAuth!!.uid!!)
        docRef.addSnapshotListener(EventListener { value, error ->
            if (error != null) {
                System.err.println("Listen failed: $error")
                return@EventListener
            }
            if (value != null && value.exists()) {
                println("Current data: " + value.data)
                val user = value.toObject(User::class.java)
                val author_uid = mAuth!!.uid
                val author_username = user!!.username
                comment = Comment(author_uid, author_username, comment_content)

                // add comment to the database
                mFirestore!!.collection("posts").document(pid!!)
                        .collection("comments")
                        .add(comment!!)
                        .addOnSuccessListener { documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.id) }
                        .addOnFailureListener { e -> Log.w(TAG, "Error adding comment\n", e) }
            } else {
                print("Current data: null")
            }
        })
    }


    companion object {
        private const val TAG = "PostDetailedActivity"
    }
}