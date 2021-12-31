package com.unlone.app.ui.create

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.unlone.app.model.Post
import java.text.ParseException
import java.util.*

class SavedStateModel : ViewModel() {

    private var _postData: MutableLiveData<PostData> = MutableLiveData(PostData())
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mFirestore = Firebase.firestore
    private val storageReference = Firebase.storage.reference
    private val _navBack: MutableLiveData<Boolean> = MutableLiveData(false)
    val navBack: LiveData<Boolean> = _navBack

    private val uid = mAuth.uid
    val postData: LiveData<PostData> = _postData
    private var _rawCategories: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
    private var _categories: MutableLiveData<List<String>> = MutableLiveData()
    val categories: LiveData<List<String>> = _categories
    private var _categoryTitle = MutableLiveData<String>()
    val categoryTitle: LiveData<String> = _categoryTitle

    init {
        loadCategories()
    }

    fun savepostData(postData: PostData) {
        _postData.value = postData
    }

    fun createPostObject(postData: PostData): Post {
        val post = Post()

        retrieveDefaultCategory(postData.category)?.let {
            post.category = it
        }

        // assign the rest of it
        if (uid != null) {
            post.author_uid = uid
            post.title = postData.title
            post.journal = postData.journal
            post.labels.addAll(postData.labels)
            post.comment = postData.comment
            post.save = postData.save
            post.createdTimestamp = System.currentTimeMillis().toString()

        }

        return post
    }

    fun submitPost(postData: PostData) {
        // Since the displaying category name may have varied language,
        // it has to be stored as the default language
        val post = createPostObject(postData)
        if (postData.imageUri == null) {
            // Upload text only
            post.imagePath = ""
            uploadText(post)
        } else {
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
                    // Toast.makeText(activity, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                // Handle unsuccessful uploads
                    e ->
                // Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(ParseException::class)
    private fun saveNewPost(post: Post) {
        mFirestore.collection("posts").add(post).addOnSuccessListener { documentReference ->
            Log.d(
                ContentValues.TAG, "DocumentSnapshot written with ID: " + documentReference.id
            )
        }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
                _navBack.value = true
            }
    }


    private fun uploadText(post: Post) {
        val stamp = System.currentTimeMillis()
        post.createdTimestamp = stamp.toString()

        try {
            saveNewPost(post)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        _navBack.value = true
    }

    @Suppress("UNCHECKED_CAST")
    fun loadCategories() {
        val deviceLanguage = Locale.getDefault().language
        Log.d("TAG", "device Language: $deviceLanguage")
        val appLanguage = when (deviceLanguage) {
            "zh" -> "zh_hk"          // if the device language is set to Chinese, use chinese text
            else -> "default"        // default language (english)
        }
        mFirestore.collection("categories")
            .document("pre_defined_categories")
            .collection("categories_name")
            .whereEqualTo("visibility", true)
            .get()
            .addOnSuccessListener { result ->
                val rawCategoryArrayList = java.util.ArrayList<Pair<String, String>>()
                for (document in result) {
                    val category = Pair(document.id, document.data[appLanguage])
                    category.let { rawCategoryArrayList.add(it as Pair<String, String>) }

                }
                _rawCategories.value = rawCategoryArrayList
                val c = ArrayList<String>()
                for (rawCategory in rawCategoryArrayList) {
                    c.add(rawCategory.second)
                }
                _categories.value = c
                Log.d("TAG category", _categories.toString())
            }
    }

    private fun retrieveDefaultCategory(selectedCategory: String): String? {
        for (c in _rawCategories.value!!) {
            if (selectedCategory in c.toList()) {
                return c.first
            }
        }
        return null
    }

}