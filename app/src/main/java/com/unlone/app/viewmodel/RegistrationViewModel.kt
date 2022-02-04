package com.unlone.app.viewmodel

import android.util.Log
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.unlone.app.R
import com.unlone.app.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@HiltViewModel
@InternalCoroutinesApi
class RegistrationViewModel @Inject constructor() : ViewModel() {
    val mAuth = Firebase.auth
    val mFireStore = Firebase.firestore
    private var functions: FirebaseFunctions = Firebase.functions

    private val _navToVerification: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _regBtnEnable: MutableLiveData<Boolean> = MutableLiveData(true)
    private val _showProgressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _showValidationErrorMsg: MutableLiveData<Boolean> = MutableLiveData(false)

    val navToVerification: MutableLiveData<Boolean> = _navToVerification
    val regBtnEnable: MutableLiveData<Boolean> = _regBtnEnable
    val showProgressBar: MutableLiveData<Boolean> = _showProgressBar
    val showValidationErrorMsg: MutableLiveData<Boolean> = _showValidationErrorMsg

    // Mutable/LiveData of String resource reference Event
    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>>
        get() = _message

    // Post in background thread
    fun postMessage(@StringRes message: Int) {
        _message.postValue(Event(message))
    }

    // Post in main thread
    private fun setMessage(@StringRes message: Int) {
        _message.value = Event(message)
    }

    private fun validateSchoolEmail(email: String): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "text" to email,
            "push" to true
        )

        return functions
            .getHttpsCallable("validateSchoolEmail")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result: HashMap<String, String> = task.result.data as HashMap<String, String>
                result.values.toList()[0]
            }
    }

    fun performRegister(_email: EditText, _password: EditText, _confirmPassword: EditText) {
        val email = _email.text.toString()
        val password = _password.text.toString()
        val confirmPassword = _confirmPassword.text.toString()
        // Register with email
        _showProgressBar.value = false
        _regBtnEnable.value = true
        if (email.isBlank()) {
            setMessage(R.string.type_email)
        } else if (password.isBlank()) {
            setMessage(R.string.type_password)
        } else if (password.length < 6) {
            setMessage(R.string.longer_password)
        } else if (password != confirmPassword) {
            setMessage(R.string.confirm_password_incorrect)
        } else {
            setMessage(R.string.validate_school_email)
            validateSchoolEmail(email)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        val e = task.exception
                        if (e is FirebaseFunctionsException) {
                            val code = e.code
                            val details = e.details
                            Log.e("TAG", "\n$code\n$details")
                        }
                        e?.toString()?.let { it1 -> Log.e("TAG", it1) }
                    } else {
                        Log.d("TAG", task.result)
                        if (task.result == "true") {
                            // school email is validated, perform register process
                            viewModelScope.launch {
                                try {
                                    mAuth.createUserWithEmailAndPassword(email, password).await()
                                    val user: FirebaseUser? = mAuth.currentUser
                                    _navToVerification.value = user != null
                                } catch (e: FirebaseAuthUserCollisionException) {
                                    _navToVerification.value = false
                                    e.message?.let { Log.d("TAG", it) }

                                }
                            }
                        } else {
                            _showValidationErrorMsg.value = true
                        }
                    }
                }
        }
    }
}