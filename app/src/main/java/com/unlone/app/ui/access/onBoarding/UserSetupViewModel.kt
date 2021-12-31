package com.unlone.app.ui.access.onBoarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unlone.app.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class UserSetupViewModel : ViewModel() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mFirestore = Firebase.firestore
    private val user = User(uid = mAuth.uid)
    private val appLanguage = when (Locale.getDefault().language) {
        "zh" -> "zh_hk"          // if the device language is set to Chinese, use chinese text
        else -> "default"        // default language (english)
    }
    private var _rawCategories: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
    private var _rawInterests: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
    private var _categories: MutableLiveData<List<String>> = MutableLiveData()
    val categories: LiveData<List<String>> = _categories
    private var _interests: MutableLiveData<List<String>> = MutableLiveData()
    val interests: LiveData<List<String>> = _interests


    init {
        loadCategories()
        loadInterests()
    }

    private fun loadInterests() {
        mFirestore.collection("interests")
            .get()
            .addOnSuccessListener { result ->
                val rawCategoryArrayList = ArrayList<Pair<String, String>>()
                for (document in result) {
                    val interest = Pair(document.id, document.data[appLanguage])
                    interest.let { rawCategoryArrayList.add(it as Pair<String, String>) }

                }
                _rawInterests.value = rawCategoryArrayList
                val i = ArrayList<String>()
                for (rawInterest in rawCategoryArrayList) {
                    i.add(rawInterest.second)
                }
                _interests.value = i
                Log.d("TAG interests", _interests.toString())
            }
    }

    private fun loadCategories() {
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

    fun setUserName(username: String) {
        user.username = username
        uploadUserData()
    }

    fun setIdentity(identity: String) {
        user.identity = identity
    }

    fun setCategories(selectedCategories: ArrayList<String>) {
        for (i in selectedCategories) {
            selectedCategories.map { retrieveDefaultCategory(it) }
        }
        // default: user follow "Unlone" category
        selectedCategories.add("unlone")
        Log.d("TAG", "added: $selectedCategories")

        // write user info into Firestore
        user.followingCategories = selectedCategories
    }

    private fun retrieveDefaultCategory(selectedCategory: String): String? {
        for (c in _rawCategories.value!!) {
            if (selectedCategory in c.toList()) {
                return c.first
            }
        }
        return null
    }

    fun setInterests(selectedInterests: ArrayList<String>) {
        for (i in selectedInterests) {
            selectedInterests.map { retrieveDefaultInterest(it) }
        }
        // write user info into Firestore
        user.interests = selectedInterests
    }

    private fun retrieveDefaultInterest(it: String): String? {
        for (c in _rawInterests.value!!) {
            if (it in c.toList()) {
                return c.first
            }
        }
        return null
    }

    private fun uploadUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            val profileUpdates = userProfileChangeRequest {
                displayName = user.username
            }
            mAuth.currentUser?.updateProfile(profileUpdates)?.await()

        }
    }



}