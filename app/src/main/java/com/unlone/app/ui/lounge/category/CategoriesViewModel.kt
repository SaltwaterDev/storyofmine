package com.unlone.app.ui.lounge.category

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.unlone.app.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.unlone.app.model.PostItemUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class CategoriesViewModel : ViewModel() {
    private var _categories: MutableLiveData<List<String>> = MutableLiveData()
    private var _rawCategories: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
    val categories: LiveData<List<String>> = _categories
    private var _categoryTitle = MutableLiveData<String>()
    val categoryTitle: LiveData<String> = _categoryTitle

    private val _posts: MutableLiveData<List<Post>> = MutableLiveData()
    private val posts: LiveData<List<Post>> = _posts
    private val mPosts = 100
    private val postList: MutableList<Post> = java.util.ArrayList()
    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()
    private var lastVisible: DocumentSnapshot? = null
    val postListUiItems = posts.map { posts ->
        posts.map {
            PostItemUiState(
                it.title,
                it.imagePath,
                it.journal.substring(0, 120.coerceAtMost(it.journal.length)),
                it.pid
            )
        }
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
                val c = java.util.ArrayList<String>()
                for (rawCategory in rawCategoryArrayList) {
                    c.add(rawCategory.second)
                }
                _categories.value = c
                Log.d("TAG category", _categories.toString())
            }
    }


    fun loadPosts(category: String, numberPost: Int, loadMore: Boolean?) {
        Log.d("TAG", "category: $category")
        viewModelScope.launch(Dispatchers.IO) {
            if (lastVisible == null || !loadMore!!) {
                postList.clear()
                Log.d(TAG, "First load/Refresh")

                val thisCategoryDocs = mFirestore.collection("posts")
                    .whereEqualTo("category", category)
                    .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                if (thisCategoryDocs != null) {
                    if (thisCategoryDocs.size() > 0) {
                        lastVisible = thisCategoryDocs.documents[thisCategoryDocs.size() - 1]
                    }
                    for (document in thisCategoryDocs) {
                        Log.d(TAG, document.id + " => " + document.data)
                        val post = document.toObject<Post>()
                        post.pid = document.id
                        if (!postList.contains(post)) {
                            postList.add(post)
                        }
                    }
                }
                // sort the postList
                val sortedPostList = postList.sortedByDescending { it.createdTimestamp }
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "sorted postList: $sortedPostList")
                    _posts.value = sortedPostList
                }
            } else {
                val thisCategoryDocs = mFirestore.collection("posts")
                    .whereEqualTo("category", category)
                    .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                    //.startAfter(lastVisible)
                    .get()
                    .await()

                if (thisCategoryDocs != null) {
                    if (thisCategoryDocs.size() > 0) {
                        lastVisible = thisCategoryDocs.documents[thisCategoryDocs.size() - 1]
                    }
                    for (document in thisCategoryDocs) {
                        Log.d(TAG, document.id + " => " + document.data)
                        val post = document.toObject<Post>()
                        post.pid = document.id
                        if (!postList.contains(post)) {
                            postList.add(post)
                        }
                    }
                }
                // sort the postList
                val sortedPostList = postList.sortedByDescending { it.createdTimestamp }
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "sorted postList: $sortedPostList")
                    _posts.value = sortedPostList
                }
            }
        }
    }

    fun searchPost(text: String) {
        // TODO ("After using firebase function")
    }

    fun followCategory(category: String, follow: Boolean) {
        Log.d("TAG", "category: $category")
        mAuth.uid?.let {
            if (follow) {
                mFirestore.collection("users").document(it)
                    .update(
                        "followingCategories", FieldValue.arrayUnion(category)
                    )
            } else {
                mFirestore.collection("users").document(it)
                    .update(
                        "followingCategories", FieldValue.arrayRemove(category)
                    )
            }
        }
    }

    fun retrieveDefaultCategory(selectedCategory: String): String? {
        for (c in _rawCategories.value!!) {
            if (selectedCategory in c.toList()) {
                return c.first
            }
        }
        return null
    }

    fun getCategoryTitle(categoryId: String) {
        val appLanguage = when (Locale.getDefault().language) {
            "zh" -> "zh_hk"          // if the device language is set to Chinese, use chinese text
            else -> "default"        // default language (english)
        }

        viewModelScope.launch(Dispatchers.Main) {
            _categoryTitle.value = withContext(Dispatchers.IO) {
                mFirestore.collection("categories")
                    .document("pre_defined_categories")
                    .collection("categories_name")
                    .document(categoryId)
                    .get()
                    .await()
                    .data?.get(appLanguage) as String?
            }!!
        }
    }

    suspend fun isFollowing(category: String): Boolean {
        val result = mFirestore.collection("users").document(mAuth.uid!!).get().await()
        return category in (result.data?.get("followingCategories") as ArrayList<*>)
    }

}