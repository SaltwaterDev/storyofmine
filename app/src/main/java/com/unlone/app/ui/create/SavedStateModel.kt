package com.unlone.app.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SavedStateModel : ViewModel() {

    private var _postData: MutableLiveData<PostData> = MutableLiveData(PostData())
    val postData: LiveData<PostData> = _postData
    fun savepostData(postData: PostData){
        _postData.value = postData
    }




}