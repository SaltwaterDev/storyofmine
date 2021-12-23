package com.unlone.app.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.unlone.app.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditProfileViewModel : ViewModel() {
    private val mFirestore = Firebase.firestore
    private val currentUser = Firebase.auth.currentUser
    private val docRef = mFirestore.collection("users").document(currentUser!!.uid)

    private val _username: MutableLiveData<String> = MutableLiveData()
    val username: LiveData<String> = _username

    private val _bio: MutableLiveData<String> = MutableLiveData()
    val bio: LiveData<String> = _bio

    private val _isSavedEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isSavedEnabled: LiveData<Boolean> = _isSavedEnabled

    private val _isCancelEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isCancelEnabled: LiveData<Boolean> = _isCancelEnabled

    private val _returnBack: MutableLiveData<Boolean> = MutableLiveData(false)
    val returnBack: LiveData<Boolean> = _returnBack

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() = viewModelScope.launch {
        val docs = docRef.get().await()
        if (docs != null) {
            println("Current data: " + docs.data)
            val user = docs.toObject<User>()
            _username.value = (user?.username) ?: ""
            _bio.value = user?.bio?: ""

        } else {
            print("Current data: null")
        }
    }

    fun updateProfile(username: String, bio: String) {
        viewModelScope.launch {
            _isSavedEnabled.value = false
            _isCancelEnabled.value = false
            docRef.update(
                "username", username,
                "bio", bio
            ).await()
            // notify fragment to go back to the previous page
            _returnBack.value = true
        }
    }

    fun goBack(){
        // notify fragment to go back to the previous page
        _returnBack.value = true
    }

}