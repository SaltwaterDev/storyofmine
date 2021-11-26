package com.unlone.app.ui.create

import android.content.ContentValues
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.unlone.app.R
import com.unlone.app.databinding.FragmentConfigBinding
import com.unlone.app.databinding.LayoutPostBinding
import com.unlone.app.instance.Post
import com.unlone.app.instance.User
import com.unlone.app.utils.convertTimeStamp
import com.unlone.app.utils.dpConvertPx
import com.unlone.app.utils.getImageHorizontalMargin
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.text.ParseException
import java.util.*


class ConfigFragment : Fragment() {

    private val savedStateModel: SavedStateModel by activityViewModels()
    private var postData: PostData? = null
    var post: Post = Post()
    private var _binding: FragmentConfigBinding? = null
    private lateinit var mergeLayoutPostBinding: LayoutPostBinding

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        val view = binding.root
        mergeLayoutPostBinding = LayoutPostBinding.bind(view)

        savedStateModel.postData.observe(viewLifecycleOwner, {postData ->
            this.postData = postData
            Log.d("TAG", "config fragment, postData: $postData")
            binding.commentSwitch.isChecked = !postData.comment
            binding.saveSwitch.isChecked = !postData.save
            binding.textField.setText(postData.category)

            // Preview
            var displayLabel = ""
            for (label in postData.labels) {
                displayLabel += "·$label "
            }
            postData.imageUri?.let { displayImage(it) }
            mergeLayoutPostBinding.textViewTitle.text = postData.title
            mergeLayoutPostBinding.date.text = convertTimeStamp((System.currentTimeMillis()).toString(), Locale.getDefault().language)
            mergeLayoutPostBinding.textViewJournal.text = postData.journal
            val labelTv = TextView(context)
            labelTv.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            context?.let {
                labelTv.typeface = ResourcesCompat.getFont(it, R.font.sf_pro_text_semibold)
                labelTv.setTextColor(ContextCompat.getColor(it, R.color.colorText))
            }
            labelTv.letterSpacing = 0.01F
            labelTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
            labelTv.text = displayLabel
            mergeLayoutPostBinding.labelGroup.addView(labelTv)

        })


        val commentSwitch = binding.commentSwitch.setOnCheckedChangeListener { _, isChecked ->
            postData?.comment = !isChecked
        }
        val saveSwitch = binding.saveSwitch.setOnCheckedChangeListener { _, isChecked ->
            postData?.save = !isChecked
        }
        val backButton = binding.backButton.setOnClickListener {
            postData?.category  = binding.textField.text.toString()
            postData?.let { it1 -> savedStateModel.savepostData(it1) }
            Navigation.findNavController(view).navigate(R.id.navigateToWritePostFragment)
        }
        val postButton = binding.postButton.setOnClickListener {
            binding.postButton.isEnabled = false
            binding.backButton.isEnabled = false
            postData?.category  = binding.textField.text.toString()
            postData?.let { it1 -> submitPost(it1) }
            binding.postButton.isEnabled = true
            binding.backButton.isEnabled = true
        }


        mFirestore = FirebaseFirestore.getInstance()
        storageReference = Firebase.storage.reference

        return view
    }


    override fun onResume() {
        super.onResume()
        // load categories
        val model = ViewModelProvider(this).get(ConfigViewModel::class.java)
        model.loadCategories()
        model.categories.observe(viewLifecycleOwner, { categories ->
            Log.d("TAG category config", categories.toString())
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, categories)
            binding.textField.setAdapter(adapter)
        })
    }

    private fun displayImage(uri: Uri) {
        mergeLayoutPostBinding.imageCover.setImageURI(uri)
        mergeLayoutPostBinding.imageCover.visibility = View.VISIBLE
        val bitmap = (mergeLayoutPostBinding.imageCover.drawable as BitmapDrawable).bitmap
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        Log.d("uri", uri.toString())

        val imageHorizontalMargin = activity?.let { getImageHorizontalMargin(width / height, it) }    // in px
        val imageVerticalMargin = activity?.let { dpConvertPx(10, it) }
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        Log.d("margin", imageVerticalMargin.toString())
        if (imageHorizontalMargin != null && imageVerticalMargin != null) {
            params.setMargins(imageHorizontalMargin, imageVerticalMargin, imageHorizontalMargin, 0)
        }
        mergeLayoutPostBinding.imageCover.layoutParams = params
    }

    private fun submitPost(postData: PostData) {
        Toast.makeText(activity, "Posting...", Toast.LENGTH_SHORT).show()
        if (postData.category.isEmpty()){
            Toast.makeText(activity, "You Haven't set the category", Toast.LENGTH_SHORT).show()
            return
        }
        // Since the displaying category name may have varied language,
        // it has to be stored as the default language
        val model = ViewModelProvider(this).get(ConfigViewModel::class.java)
        model.retrieveDefaultCategory(postData.category)?.let {
            post.category = it
        }

        // assign the rest of it
        post.author_uid = postData.uid
        post.title = postData.title
        post.journal = postData.journal
        post.labels.addAll(postData.labels)
        post.comment = postData.comment
        post.save = postData.save

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
        val docRef = mFirestore.collection("users").document(post.author_uid)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.data)
                    val user = document.toObject<User>()
                    post.username = user!!.username.toString()
                    val stamp = System.currentTimeMillis()
                    post.createdTimestamp = stamp.toString()
                    post.createdDate = convertTimeStamp(stamp.toString(), Locale.getDefault().language)

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

    companion object
}