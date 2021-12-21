package com.unlone.app.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SavedStateModel : ViewModel() {

    private var _postData: MutableLiveData<PostData> = MutableLiveData(PostData())
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val uid = mAuth.uid
    val postData: LiveData<PostData> = _postData
    fun savepostData(postData: PostData) {
        _postData.value = postData
    }


}