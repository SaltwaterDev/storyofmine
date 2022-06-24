package com.unlone.app.write

import kotlinx.coroutines.flow.Flow


interface DraftRepository {
    fun getAllDraftTitles(): Flow<List<String>>
    fun getCurrentDraft(): Flow<ParentDraftRealmObject?>
    suspend fun saveDraft(title: String, content: String)
}