package com.example.app.data.write

import com.example.app.domain.entities.Draft
import kotlinx.coroutines.flow.Flow


interface DraftRepository {
    fun getAllDrafts(): Flow<List<Draft>>
    fun queryDraft(id: String): Flow<Draft>
    fun getLastOpenedDraft(): Flow<Draft?>
    suspend fun createNewDraft(title: String, body: String): Pair<String, String>
    suspend fun updateLastOpenedTime(id: String)
    suspend fun deleteDraft(id: String)
    suspend fun updateDraftVersion(
        draftId: String,
        title: String,
        body: String
    ): Pair<String, String>

    suspend fun addNewVersionToDraft(
        id: String,
        title: String,
        body: String
    ): Pair<String?, String?>
}