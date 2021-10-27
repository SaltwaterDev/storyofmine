package com.example.unlone.ui.lounge.category

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unlone.instance.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class CategoriesViewModel: ViewModel() {
    private var _categories: MutableLiveData<List<String>> = MutableLiveData()
    private var _rawCategories: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
    var categories:  LiveData<List<String>> = _categories
    private var _categoryTitle = MutableLiveData<String>()
    val categoryTitle: LiveData<String> = _categoryTitle

    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val mAuth = FirebaseAuth.getInstance()
    private val postList: MutableList<Post>
    private var lastVisible: DocumentSnapshot? = null
    fun getPosts(): LiveData<List<Post>> {
        return posts
    }

    @Suppress("UNCHECKED_CAST")
    fun loadCategories() {
        val language = Locale.getDefault().language
        Log.d("TAG", "language: $language")
        if (language == "zh"){
            // if the device language is set to Chinese, use chinese text
            mFirestore.collection("categories")
                .document("pre_defined_categories")
                .collection("categories_name")
                .get()
                .addOnSuccessListener { result  ->
                    val rawCategoryArrayList = java.util.ArrayList<Pair<String, String>>()
                    for (document in result) {
                        val category = Pair(document.id, document.data["zh_hk"])
                        category.let { rawCategoryArrayList.add(it as Pair<String, String>) }

                    }
                    _rawCategories.value = rawCategoryArrayList
                    val c = java.util.ArrayList<String>()
                    for (rawCategory in rawCategoryArrayList){
                        c.add(rawCategory.second)
                    }
                    _categories.value = c
                    Log.d("TAG category", _categories.toString())
                }
        }else {
            // default language (english)
            mFirestore.collection("categories")
                .document("pre_defined_categories")
                .collection("categories_name")
                .get()
                .addOnSuccessListener { result ->
                    val rawCategoryArrayList = java.util.ArrayList<Pair<String, String>>()
                    for (document in result) {
                        val category = Pair(document.id, document.data["default"])
                        category.let { rawCategoryArrayList.add(it as Pair<String, String>) }
                    }
                    _rawCategories.value = rawCategoryArrayList
                    val c = java.util.ArrayList<String>()
                    for (rawCategory in rawCategoryArrayList){
                        c.add(rawCategory.second)
                    }
                    _categories.value = c
                    Log.d("TAG category", _categories.toString())
                }
        }
    }


    init {
        postList = ArrayList()
    }

    fun loadPosts(category: String, numberPost: Int, loadMore: Boolean?) {
        Log.d("TAG", "category: $category")
        if (lastVisible == null || !loadMore!!) {
            postList.clear()
            Log.d(TAG, "First load")
            mFirestore.collection("posts")
                .whereEqualTo("category", category)
                .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                .limit(numberPost.toLong())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let{ results ->
                            if (results.size() > 0) {
                                lastVisible = results.documents[results.size() - 1]
                            }
                            for (document in results) {
                                Log.d(TAG, document.id + " => " + document.data)
                                val post = document.toObject<Post>()
                                post.pid = document.id
                                if (!postList.contains(post)) {
                                    postList.add(post)
                                    posts.value = postList
                                }
                            }
                        }
                    }else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }
        } else {
            mFirestore.collection("posts")
                .whereEqualTo("category", category)
                .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(numberPost.toLong())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let{ results ->
                            if (results.size() > 0) {
                                for (document in results) {
                                    Log.d(TAG, document.id + " => " + document.data)
                                    val post = document.toObject(Post::class.java)
                                    post.pid = document.id
                                    if (!postList.contains(post)) {
                                        postList.add(post)
                                        posts.value = postList
                                    }
                                }
                                lastVisible =
                                    results.documents[task.result!!.size() - 1]
                            } else {
                                Log.d(TAG, "End of posts")}
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }
        }
    }

    fun searchPost(text: String){
        // TODO ("After using firebase function")
    }


    fun followCategory(category: String, follow: Boolean){
        Log.d("TAG", "category: $category")
        mAuth.uid?.let {
            if(follow){
                mFirestore.collection("users").document(it)
                    .update(
                        "followingCategories", FieldValue.arrayUnion(category)
                    )
            } else{
                    mFirestore.collection("users").document(it)
                        .update(
                            "followingCategories", FieldValue.arrayRemove(category)
                        )
            }
        }
    }

    fun retrieveDefaultCategory(selectedCategory: String): String? {
        for (c in _rawCategories.value!!){
            if (selectedCategory in c.toList()){
                return c.first
            }
        }
        return null
    }

    fun getCategoryTitle(categoryId: String){
        viewModelScope.launch(Dispatchers.Main){
            _categoryTitle.value = withContext(Dispatchers.IO){
                mFirestore.collection("categories")
                    .document("pre_defined_categories")
                    .collection("categories_name")
                    .document(categoryId)
                    .get()
                    .await()
                    .data?.get("zh_hk") as String?
            }!!
        }
    }
}