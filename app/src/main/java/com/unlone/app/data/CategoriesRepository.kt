package com.unlone.app.data


import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CategoriesRepository @Inject constructor(
    userRepository: UserRepository,
    private val externalScope: CoroutineScope,
) {
    private val appLanguage: StateFlow<String?> =
        userRepository.getFireStoreLocale().stateIn(externalScope, Lazily, null)
    private val mFirestore = Firebase.firestore
    private val mAuth = Firebase.auth

    val rawCategories: StateFlow<Map<String, String>> = appLanguage.filterNotNull().map {
        loadDefaultCategories(it)
    }.stateIn(
        externalScope,
        Lazily,
        mapOf()
    )


    val categories: StateFlow<List<String>> =
        rawCategories.map { it.values.toList() }.stateIn(externalScope, Lazily, emptyList())


    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun loadDefaultCategories(appLang: String): MutableMap<String, String> {
        Timber.d("language: $appLang")
        val rawCategoriesSnapshot = withContext(Dispatchers.IO) {
            mFirestore.collection("categories")
                .document("pre_defined_categories")
                .collection("categories_name")
                .whereEqualTo("visibility", true)
                .get()
                .await()
        }
        val rawCategoryMap = mutableMapOf<String, String>()
        for (document in rawCategoriesSnapshot) {
            rawCategoryMap[document.id] = document.data[appLang] as String
        }
        Timber.d("rawCategoryMap: $rawCategoryMap")
        return rawCategoryMap
    }


    suspend fun isFollowing(category: String): Boolean {
        val result = mFirestore.collection("users").document(mAuth.uid!!).get().await()
        return category in (result.data?.get("followingCategories") as ArrayList<*>)
    }

    suspend fun getTopicTitle(categoryId: String): String? {
        Timber.d("categoryId: $categoryId")
        val titleCollection = withContext(Dispatchers.IO) {
            mFirestore.collection("categories")
                .document("pre_defined_categories")
                .collection("categories_name")
                .whereEqualTo("visibility", true)
                .get()
                .await()
        }

        Timber.d("language: ${appLanguage.value}")

        val result = titleCollection.documents.filter { it.id == categoryId }
        return if (!result.isNullOrEmpty())
            result[0].data?.get(appLanguage.value).toString()
        else
            null
    }

    fun retrieveDefaultTopic(selectedTopic: String): Flow<String> {
        return rawCategories.map { it.filter { item -> item.value == selectedTopic }.keys.first() }
    }

    fun followCategory(category: String, follow: Boolean) {
        mAuth.uid?.let {
            if (follow) {
                // follow the topic
                mFirestore.collection("users").document(it)
                    .update(
                        "followingCategories", FieldValue.arrayUnion(category)
                    )
            } else {
                // unfollow the topic
                mFirestore.collection("users").document(it)
                    .update(
                        "followingCategories", FieldValue.arrayRemove(category)
                    )
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadFollowingTopics(): Flow<List<String>> = appLanguage.map {

        val topicKeys = mFirestore.collection("users")
            .document(mAuth.uid!!).get().await().get("followingCategories") as List<String>

        Timber.d("topicKeys: $topicKeys")
        val topicList: List<String> = topicKeys.map {
            val isLabel = it.first() == '#'
            if (isLabel)
                it
            else
                getTopicTitle(it)
        }.filterNotNull()
        topicList
    }
}


