package com.unlone.app.data.write

import com.unlone.app.domain.entities.Draft
import kotlinx.coroutines.flow.Flow


interface DraftRepository {
    fun getAllDrafts(): Flow<List<Draft>>
    fun queryDraft(id: String): Flow<Draft>
    fun getLastOpenedDraft(): Flow<Draft?>
    suspend fun saveDraft(id: String?, title: String, body: String): Pair<String, String>
    suspend fun updateLastOpenedTime(id: String)
    suspend fun deleteDraft(id: String)
    suspend fun updateDraftVersion(
        parentDraftId: String,
        title: String,
        body: String
    ): Pair<String, String>
}