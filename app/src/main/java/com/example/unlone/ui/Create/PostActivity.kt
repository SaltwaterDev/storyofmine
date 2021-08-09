package com.example.unlone.ui.Create

import android.Manifest
import androidx.annotation.RequiresApi
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.example.unlone.ui.Create.CreateViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import android.os.Bundle
import com.example.unlone.R
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.storage.FirebaseStorage
import kotlin.Throws
import com.google.firebase.firestore.DocumentSnapshot
import android.content.ContentValues
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.unlone.ui.Create.PostActivity
import android.content.ContentResolver
import android.webkit.MimeTypeMap
import com.theartofdev.edmodo.cropper.CropImage
import android.content.Intent
import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.*
import com.example.unlone.instance.Post
import com.example.unlone.instance.User
import com.google.android.gms.tasks.*
import com.google.firebase.storage.UploadTask
import java.lang.Exception
import java.text.ParseException
import java.time.LocalDateTime
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class PostActivity : AppCompatActivity() {
    private lateinit var createViewModel: CreateViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    private lateinit var journal: EditText
    private lateinit var title: EditText
    private lateinit var imagePost: ImageView
    private lateinit var post_button: TextView
    private lateinit var cancel_button: TextView

    var localDateTime = LocalDateTime.now().toString()
    private lateinit var setSelectedImagePath: String
    lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // init database storing
        createViewModel = ViewModelProvider(this).get(CreateViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("posts")

        // init toolbar
        post_button = findViewById(R.id.post_button)
        cancel_button = findViewById(R.id.cancel_button)
        post_button.setOnClickListener(View.OnClickListener { submitPost() })
        cancel_button.setOnClickListener(View.OnClickListener { onBackPressed() })
        title = findViewById(R.id.inputPostTitle)
        journal = findViewById(R.id.inputPostContext)
        imagePost = findViewById(R.id.imagePost)
        initMiscellaneous()
    }

    private fun getImageStorageUrl(uploadTask: UploadTask, ref: StorageReference): String {
        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
            }
        }
        return urlTask.toString()
    }

    private fun submitPost() {
        val pTitle = title.text.toString()
        val pJournal = journal.text.toString()
        if (pJournal.isEmpty()) {
            Toast.makeText(applicationContext, "You did not complete the post.", Toast.LENGTH_SHORT).show()
            return
        }
        setEditingEnabled(false)
        Toast.makeText(applicationContext, "Posting...", Toast.LENGTH_SHORT).show()
        val uid = mAuth.uid!!

        // Upload Image
        val imageUUID = UUID.randomUUID().toString()
        val ref = storageReference.child(imageUUID)
        var uploadTask = ref.putFile(selectedImageUri)

        // get image url
        var downloadUri: Uri?
        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setSelectedImagePath = task.result.toString()
                Log.d("iamgepath", setSelectedImagePath)

                val docRef = mFirestore.collection("users").document(uid)
                docRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document!!.exists()) {
                            Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.data)
                            val user = document.toObject(User::class.java)
                            try {
                                saveNewPost(uid, user!!.username, pTitle, setSelectedImagePath, pJournal, localDateTime)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            setEditingEnabled(true)
                            finishPosting()
                            finish()
                        } else {
                            Log.d(ContentValues.TAG, "No such document")
                        }
                    } else {
                        Log.d(ContentValues.TAG, "get failed with ", task.exception)
                    }
                }

            } else {
                // Handle failures
            }
        }.addOnFailureListener {
            // Handle unsuccessful uploads
            e ->
            Toast.makeText(this@PostActivity, e.message, Toast.LENGTH_SHORT).show()
        }



        /*val fileReference = storageReference!!.child(System.currentTimeMillis()
            .toString() + "." + getFileExtension(selectedImageUri!!))
    uploadTask = fileReference.putFile(selectedImageUri!!)
    uploadTask.continueWithTask(object : Continuation<Any?, Any?> {
        @Throws(Exception::class)
        override fun then(task: Task<*>): Any? {
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            return fileReference.downloadUrl
        }
    }).addOnCompleteListener(object : OnCompleteListener<Any?> {
        override fun onComplete(task: Task<*>) {
            if (task.isSuccessful) {
                val downloadUri = (task.result as Uri?)!!
                setSelectedImagePath = downloadUri.toString()
                Toast.makeText(this@PostActivity, "setSelectedImagePath: $setSelectedImagePath", Toast.LENGTH_SHORT).show()

                //upload rest of the content
                val docRef = mFirestore!!.collection("users").document(uid)
                docRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document!!.exists()) {
                            Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.data)
                            val user = document.toObject(User::class.java)
                            try {
                                saveNewPost(uid, user!!.username, pTitle, setSelectedImagePath, pJournal, localDateTime)
                            } catch (e: ParseException) {
                                e.printStackTrace()
                            }
                            setEditingEnabled(true)
                            finishPosting()
                            finish()
                        } else {
                            Log.d(ContentValues.TAG, "No such document")
                        }
                    } else {
                        Log.d(ContentValues.TAG, "get failed with ", task.exception)
                    }
                }
            } else {
                Toast.makeText(this@PostActivity, "Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }).addOnFailureListener(OnFailureListener { e -> Toast.makeText(this@PostActivity, e.message, Toast.LENGTH_SHORT).show() })
*/
    }

    private fun setEditingEnabled(enabled: Boolean) {
        title.isEnabled = enabled
        journal.isEnabled = enabled
    }

    private fun initMiscellaneous() {
        val layoutMiscellaneous = findViewById<LinearLayout>(R.id.layoutMore)
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous)
        layoutMiscellaneous.findViewById<View>(R.id.textMore).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
        layoutMiscellaneous.findViewById<View>(R.id.layoutAddImage).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if (ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this@PostActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                selectImage()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage()
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun selectImage() {
        CropImage.activity()
                .start(this@PostActivity)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)!!
            selectedImageUri = result.uri
            try {
                imagePost!!.setImageURI(selectedImageUri)
                imagePost!!.visibility = View.VISIBLE
            } catch (exception: Exception) {
                exception.message?.let { Log.d("onActivityResult", it) }
                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    @Throws(ParseException::class)
    private fun saveNewPost(uid: String?, username: String, title: String?, ImagePath: String?, journal: String, createdDateTime: String) {
        val post = Post(uid, username, journal, createdDateTime)
        post.setCreatedDate()
        if (title != null) {
            Log.d("CREATEFRAGMENT", title)
            post.title = title
        }
        if (ImagePath != null) {
            Log.d("CREATEFRAGMENT", ImagePath)
            post.imagePath = ImagePath
        }
        mFirestore!!.collection("posts").add(post).addOnSuccessListener { documentReference -> Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: " + documentReference.id) }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                    setEditingEnabled(true)
                }
    }

    private fun finishPosting() {
        title.setText(null)
        journal.setText(null)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getPathFromUri(contentUri: Uri): String? {
        val filePath: String?
        val cursor = contentResolver
                .query(contentUri, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
    }
}