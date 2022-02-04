package com.unlone.app.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.unlone.app.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel: ViewModel() {
    private var mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val docRef = mFirestore.collection("users").document(
        currentUser!!.uid
    )

    private val _username: MutableLiveData<String> = MutableLiveData()
    val username: LiveData<String> = _username

    private val _bio: MutableLiveData<String> = MutableLiveData()
    val bio: LiveData<String> = _bio

    private val _bioVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val bioVisibility: LiveData<Int> = _bioVisibility


    fun loadUserProfile() = viewModelScope.launch {
        val docs = docRef.get().await()
        if (docs != null) {
            println("Current data: " + docs.data)
            val user = docs.toObject<User>()
            _username.value = (user!!.username)
            _bio.value = user.bio?: ""
            if (!bio.value.isNullOrBlank()) {
                _bioVisibility.value = View.VISIBLE
            }else{
                _bioVisibility.value = View.GONE
            }
        } else {
            print("Current data: null")
        }
    }
}