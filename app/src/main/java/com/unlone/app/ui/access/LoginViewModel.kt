package com.unlone.app.ui.access

import android.text.TextUtils
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unlone.app.R
import com.unlone.app.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@InternalCoroutinesApi
class LoginViewModel @Inject constructor(): ViewModel() {
    val mAuth = Firebase.auth
    val mFireStore = Firebase.firestore
    private var _userExistedInFireStore: MutableLiveData<Boolean?> = MutableLiveData()
    val userExistedInFireStore: LiveData<Boolean?> = _userExistedInFireStore
    private val _btnEnable: MutableLiveData<Boolean> = MutableLiveData(true)
    val btnEnable: MutableLiveData<Boolean> = _btnEnable
    private val _showProgressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val showProgressBar: MutableLiveData<Boolean> = _showProgressBar

    // Mutable/LiveData of String resource reference Event
    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>>
        get() = _message

    private fun isUserExisted(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = mFireStore.collection("users").document(uid).get().await()
                withContext(Dispatchers.Main){
                    _userExistedInFireStore.value = result.data != null
                }
            } catch (e: FirebaseFirestoreException) {
                Log.d("TAG", "FirebaseFirestoreException: $e")
            }
        }
    }

    fun performLogin(_username: TextInputEditText, _password: TextInputEditText) {
        val username = _username.text.toString()
        val password = _password.text.toString()
        _btnEnable.value = false
        _showProgressBar.value = true
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            _showProgressBar.value = false
            setMessage(R.string.login_require_all)
        } else {
            mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        isUserExisted(mAuth.currentUser!!.uid)
                    else {
                        setMessage(R.string.auth_fail)
                        _showProgressBar.value = false
                        _btnEnable.value = true
                    }
                }
        }

    }

    // Post in background thread
    private fun postMessage(@StringRes message: Int) {
        _message.postValue(Event(message))
    }

    // Post in main thread
    private fun setMessage(@StringRes message: Int) {
        _message.value = Event(message)
    }


}