package com.example.unlone.ui.profile

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unlone.instance.Issue
import com.example.unlone.instance.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ContactUsViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val mAuth = FirebaseAuth.getInstance()
    private var _issueList: MutableLiveData<List<String>> = MutableLiveData()
    var issueList:  LiveData<List<String>> = _issueList



    fun loadIssueList(){
        val issuesList = ArrayList<String>()
        viewModelScope.launch (Dispatchers.IO){ // launch a new coroutine and continue
            val result = async{
                mFirestore.collection("issues")
                    .get()
                    .await()
            }
            withContext(Dispatchers.Main){
                for (document in result.await()) {
                    issuesList.add(document.id)
                }
                Log.d("TAG","issuesList: $issuesList") // main coroutine continues while a previous one is delayed
            }
        }
        _issueList.value = issuesList
    }

    fun uploadIssue(issueType: String, detail: String){
        val issue = Issue(
            issueType,
            detail,
            mAuth.uid!!
        )
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