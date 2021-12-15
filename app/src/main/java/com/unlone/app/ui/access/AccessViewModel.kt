package com.unlone.app.ui.access

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AccessViewModel : ViewModel() {
    val mAuth = FirebaseAuth.getInstance()
    val mFireStore = FirebaseFirestore.getInstance()

    private var _userExistedInFireStore: MutableLiveData<Boolean> = MutableLiveData()
    val userExistedInFireStore: LiveData<Boolean> = _userExistedInFireStore

    fun isUserExisted(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = mFireStore.collection("users").document(uid).get().await()
            if (result != null) {
                _userExistedInFireStore.value = true
            }
        }
    }

}