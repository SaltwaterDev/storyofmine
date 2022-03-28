package com.unlone.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unlone.app.data.UserRepository.Companion.LOCALE_KEY
import com.unlone.app.model.HomeUiModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class TipsRepository @Inject constructor(
    private val userRepository: UserRepository
) {
    private val firestore: FirebaseFirestore = Firebase.firestore

    fun loadTips() = userRepository.getLocale().filterNotNull().map {
        Timber.d("locale: $it")
        val collection = firestore.collection("tips").whereEqualTo("language", it).get().await()
        collection.documents.map { doc ->
            HomeUiModel.Tips(
                title = (doc["title"] ?: "") as String,
                content = (doc["content"] ?: "") as String,
                action = (doc["action"] ?: "") as String,
                actionType = (doc["action_type"] ?: "") as String
            )
        }
    }
}
