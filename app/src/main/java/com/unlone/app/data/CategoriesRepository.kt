package com.unlone.app.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CategoriesRepository @Inject constructor() {
    private var rawCategories = mutableListOf<Pair<String, String>>()

    private val mAuth = Firebase.auth
    private val mFirestore = Firebase.firestore
    private var appLanguage: String = when (Locale.getDefault().language) {
        "zh" -> "zh_hk"          // if the device language is set to Chinese, use chinese text
        else -> "default"        // default language (english)
    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            loadCategories()
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun loadCategories(): ArrayList<String> {
        val deviceLanguage = Locale.getDefault().language
        Log.d("TAG", "device Language: $deviceLanguage")
        val appLanguage = when (deviceLanguage) {
            "zh" -> "zh_hk"          // if the device language is set to Chinese, use chinese text
            else -> "default"        // default language (english)
        }

        val rawCategoriesSnapshot = withContext(Dispatchers.IO) {
            mFirestore.collection("categories")
                .document("pre_defined_categories")
                .collection("categories_name")
                .whereEqualTo("visibility", true)
                .get()
                .await()
        }

        val rawCategoryArrayList = ArrayList<Pair<String, String>>()
        for (document in rawCategoriesSnapshot) {
            val category = Pair(document.id, document.data[appLanguage])
            category.let { rawCategoryArrayList.add(it as Pair<String, String>) }
        }

        val categories = ArrayList<String>()
        for (rawCategory in rawCategoryArrayList) {
            categories.add(rawCategory.second)
        }
        rawCategories = rawCategoryArrayList

        Log.d("TAG category", categories.toString())
        return categories
    }

    suspend fun isFollowing(category: String): Boolean {
        val result = mFirestore.collection("users").document(mAuth.uid!!).get().await()
        return category in (result.data?.get("followingCategories") as ArrayList<*>)
    }

    suspend fun getTopicTitle(categoryId: String) = withContext(Dispatchers.IO) {
        mFirestore.collection("categories")
            .document("pre_defined_categories")
            .collection("categories_name")
            .document(categoryId)
            .get()
            .await()
            .data?.get(appLanguage) as String?
    }

    fun retrieveDefaultTopic(selectedTopic: String): String? {
        for (c in rawCategories) {
            if (selectedTopic in c.toList()) {
                return c.first
            }
        }
        return null
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

    suspend fun loadFollowingTopics(): List<String> {
        // retrieve the following categories first
        val topicKeys = withContext(Dispatchers.IO) {
            mFirestore.collection("users")
                .document(mAuth.uid!!)
                .get()
                .await()
                .data
                ?.get("followingCategories") as List<String>
        }

        val topics = if (topicKeys.isNullOrEmpty()) emptyList() else topicKeys.map {
            Log.d("TAG", "following topic key: ${(it)}")
            val isLabel = it.first() == '#'
            if (!isLabel){
                Log.d("TAG", "following categories: ${getTopicTitle(it)}")
                getTopicTitle(it) ?: "No Such Topic"
            }
            else{
                it
            }
        }

        return topics
    }
}