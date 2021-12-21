package com.unlone.app.ui.profile

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlone.app.model.Issue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class ContactUsViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val mAuth = FirebaseAuth.getInstance()
    private var _rawIssueMap: MutableLiveData<MutableMap<String, String>> = MutableLiveData()
    private var _issueList: MutableLiveData<List<String>> = MutableLiveData()
    var issueList:  LiveData<List<String>> = _issueList

    fun loadIssueList() {

        viewModelScope.launch(Dispatchers.IO) { // launch a new coroutine and continue
            val result = async {
                mFirestore.collection("issues")
                    .get()
                    .await()
            }
            withContext(Dispatchers.Main) {
                val rawIssuesMap = mutableMapOf<String, String>()
                val language = Locale.getDefault().language
                Log.d("TAG", "language: $language")
                if (language == "zh") {
                    // if the device language is set to Chinese, use chinese text
                    for (document in result.await()) {
                        rawIssuesMap[document["zh_hk"].toString()] = document.id
                    }
                    Log.d("TAG", "rawIssuesMap: $rawIssuesMap")
                } else {
                    // default language (english)
                    for (document in result.await()) {
                        rawIssuesMap[document["default"].toString()] = document.id
                    }
                    Log.d("TAG", "rawIssuesList: $rawIssuesMap")
                }
                _rawIssueMap.value = rawIssuesMap
                _issueList.value = rawIssuesMap.keys.toList()
            }
        }
    }

    fun uploadIssue(issueType: String, detail: String){
        val defaultIssueType = _rawIssueMap.value?.get(issueType)
        val issue = defaultIssueType?.let {
            Issue(
                it,
                detail,
                mAuth.uid!!
            )
        }
        if (issue != null) {
            mFirestore.collection("issues")
                .document(issue.issueType)
                .collection("issue List")
                .add(issue)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }

}