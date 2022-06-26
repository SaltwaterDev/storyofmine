package com.unlone.app.data.write

import com.unlone.app.domain.entities.ParentDraft
import kotlinx.coroutines.flow.Flow


interface DraftRepository {
    fun getAllDrafts(): Flow<List<ParentDraft>>
    fun queryDraft(id: String): Flow<ParentDraft>
    fun getLastEditedDraft(): Flow<ParentDraft?>
    fun saveDraft(id: String?, title: String, content: String)
}