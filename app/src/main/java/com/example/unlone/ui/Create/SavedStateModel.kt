package com.example.unlone.ui.Create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unlone.instance.PostData
import java.util.ArrayList

class SavedStateModel : ViewModel() {

    /*
    private var _title = MutableLiveData("")
    private var _imageUri: MutableLiveData<Uri> = MutableLiveData()
    private var _journal = MutableLiveData("")
    private var _uid = MutableLiveData("")
    private var _labels: MutableLiveData< ArrayList<String>> = MutableLiveData()

    val title: LiveData<String> = _title
    val imageUri: MutableLiveData<Uri> = _imageUri
    val journal: LiveData<String> = _journal
    val uid: LiveData<String> = _uid
    val labels: MutableLiveData< ArrayList<String>> = _labels

    fun saveTitle(title: String){
        _title.value = title
    }
    fun saveimageUri(imageUri: Uri){
        _imageUri.value = imageUri
    }

    fun saveJournal(journal: String){
        _journal.value = journal
    }
    fun saveUid(uid: String){
        _uid.value = uid
    }
    fun saveLabels(labels: ArrayList<String>){
            _labels.value = labels
    }*/




    private var _postData: MutableLiveData<PostData> = MutableLiveData(PostData())
    val postData: LiveData<PostData> = _postData
    fun savepostData(postData: PostData){
        _postData.value = postData
    }


}