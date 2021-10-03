package com.example.unlone.ui.lounge

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unlone.instance.Comment
import com.example.unlone.instance.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CategoriesViewModel: ViewModel() {
    private var _categories: MutableLiveData<List<String>> = MutableLiveData()
    var categories:  LiveData<List<String>> = _categories
    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    @Suppress("UNCHECKED_CAST")
    fun loadCategories() {
        mFirestore.collection("categories")
            .document("pre_defined_categories")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val data: Map<String, Any> = documentSnapshot.data as Map<String, Any>
                _categories.value = data["list"] as List<String>?
                Log.d("TAG category", _categories.toString())
            }
    }

}