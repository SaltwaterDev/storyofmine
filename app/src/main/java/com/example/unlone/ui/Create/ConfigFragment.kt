package com.example.unlone.ui.Create

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.unlone.R
import com.example.unlone.databinding.FragmentConfigBinding
import com.example.unlone.instance.Post
import com.example.unlone.instance.PostData
import com.example.unlone.instance.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.sql.Timestamp
import java.text.ParseException
import java.util.*


class ConfigFragment : Fragment() {
    val args: ConfigFragmentArgs by navArgs()
    lateinit var postData: PostData
    var post: Post = Post()
    private var _binding: FragmentConfigBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        val view = binding.root

        postData = args.postData

        Log.d("labelll", postData.labels.toString())

        val commentSwitch = binding.commentSwitch.setOnCheckedChangeListener { _, isChecked ->
            post.comment = isChecked
        }
        val saveSwitch = binding.saveSwitch.setOnCheckedChangeListener { _, isChecked ->
            post.save = isChecked
        }
        val backButton = binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        val postButton = binding.postButton.setOnClickListener {
            submitPost()
        }

        mFirestore = FirebaseFirestore.getInstance()
        storageReference = Firebase.storage.reference

        return view
    }

    private fun submitPost() {
        Toast.makeText(activity, "Posting...", Toast.LENGTH_SHORT).show()

        post.uid = postData.uid
        post.title = postData.title
        post.journal = postData.journal
        post.labels.addAll(postData.labels)
        Log.d("TAG", postData.labels.toString())
        Log.d("TAG", post.labels.toString())
        if(postData.imageUri == null){
            // Upload text only
            post.imagePath = ""
            uploadText(post)
        }else{
            // Upload Image and Text
            val imageUUID = UUID.randomUUID().toString()
            val ref = storageReference.child(imageUUID)
            val uploadTask = ref.putFile(postData.imageUri!!)

            // get image url
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    post.imagePath = task.result.toString()
                    // upload the rest of the content
                    uploadText(post)
                } else {
                    Toast.makeText(activity, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                // Handle unsuccessful uploads
                e ->
                Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    @Throws(ParseException::class)
    private fun saveNewPost(post: Post) {
        mFirestore.collection("posts").add(post).addOnSuccessListener { documentReference -> Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: " + documentReference.id) }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                    activity?.finish()
                }
    }



    private fun uploadText(post: Post) {
        val docRef = mFirestore.collection("users").document(post.uid)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.data)
                    val user = document.toObject(User::class.java)
                    post.username = user!!.username
                    val stamp = Timestamp(System.currentTimeMillis())
                    post.createdTimestamp = stamp.toString()
                    post.createdDate = Date(stamp.getTime()).toString()

                    try {
                        saveNewPost(post)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    val returnIntent = Intent()
                    returnIntent.putExtra("result", 1)
                    activity?.setResult(AppCompatActivity.RESULT_OK, returnIntent)
                    activity?.finish()
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            } else {
                Log.d(ContentValues.TAG, "get failed with ", task.exception)
            }
        }
    }

    companion object {
    }
}