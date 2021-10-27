package com.example.unlone.ui.create

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unlone.instance.Comment
import com.example.unlone.instance.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ConfigViewModel: ViewModel() {
    private var _categories: MutableLiveData<List<String>> = MutableLiveData()
    private var _rawCategories: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
    var categories:  LiveData<List<String>> = _categories
    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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
                    val rawCategoryArrayList = ArrayList<Pair<String, String>>()
                    for (document in result) {
                        val category = Pair(document.id, document.data["zh_hk"])
                        category.let { rawCategoryArrayList.add(it as Pair<String, String>) }

                    }
                    _rawCategories.value = rawCategoryArrayList
                    val c = ArrayList<String>()
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
                    val rawCategoryArrayList = ArrayList<Pair<String, String>>()
                    for (document in result) {
                        val category = Pair(document.id, document.data["default"])
                        category.let { rawCategoryArrayList.add(it as Pair<String, String>) }
                    }
                    _rawCategories.value = rawCategoryArrayList
                    val c = ArrayList<String>()
                    for (rawCategory in rawCategoryArrayList){
                        c.add(rawCategory.second)
                    }
                    _categories.value = c
                    Log.d("TAG category", _categories.toString())
                }
        }
    }

    fun retrieveDefaultCategory(category: String): String? {
        for (c in _rawCategories.value!!){
            if (category in c.toList()){
                return c.first
            }
        }
        return null
    }

}