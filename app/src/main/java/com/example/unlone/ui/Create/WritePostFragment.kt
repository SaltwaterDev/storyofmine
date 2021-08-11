package com.example.unlone.ui.Create

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.unlone.R
import com.example.unlone.databinding.FragmentWritePostBinding
import com.example.unlone.instance.Post
import com.example.unlone.instance.User
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import java.text.ParseException
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class WritePostFragment : Fragment() {
    private lateinit var createViewModel: CreateViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    private lateinit var journal: EditText
    private lateinit var title: EditText
    private lateinit var imagePost: ImageView

    private lateinit var setSelectedImagePath: String
    private var selectedImageUri: Uri? = null


    private var _binding: FragmentWritePostBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    val post: Post = Post()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        _binding = FragmentWritePostBinding.inflate(inflater, container, false)
        val view = binding.root


        // init database storing
        createViewModel = ViewModelProvider(this).get(CreateViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("posts")

        // init toolbar
        binding.cancelButton.setOnClickListener(View.OnClickListener { activity?.onBackPressed() })
        title = binding.inputPostTitle
        journal = binding.inputPostContext
        imagePost = binding.imagePost
        val nextButton = binding.nextButton.setOnClickListener {
            if (journal.text.toString().isEmpty()) {
                Toast.makeText(activity, "You did not complete the post.", Toast.LENGTH_SHORT).show()
            }else{
                post.uid = mAuth.uid!!
                post.title = title.text.toString()
                post.journal = journal.text.toString()
                post.imageUri = selectedImageUri

                val action = WritePostFragmentDirections.navigateToConfigFragment(post)
                Navigation.findNavController(view).navigate(action)
            }
        }

        initMiscellaneous()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // todo: check if this functions useful anymore
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



    private fun initMiscellaneous() {
        val layoutMore = binding.moreOverlay.layoutMore
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutMore)
        layoutMore.findViewById<View>(R.id.textMore).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
        layoutMore.findViewById<View>(R.id.layoutAddImage).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if (activity?.let { it1 ->
                        ContextCompat.checkSelfPermission(
                                it1.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    } != PackageManager.PERMISSION_GRANTED) {
                activity?.let { it1 ->
                    ActivityCompat.requestPermissions(
                            it1, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_CODE_STORAGE_PERMISSION
                    )
                }
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
                Toast.makeText(activity, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun selectImage() {
        activity?.let {
            CropImage.activity()
                .start(it)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val result = CropImage.getActivityResult(data)!!
            selectedImageUri = result.uri
            try {
                imagePost.setImageURI(selectedImageUri)
                imagePost.visibility = View.VISIBLE
            } catch (exception: Exception) {
                exception.message?.let { Log.d("onActivityResult", it) }
                Toast.makeText(activity, exception.message, Toast.LENGTH_LONG).show()
                activity?.finish()
            }
        }
    }



    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
    }

}