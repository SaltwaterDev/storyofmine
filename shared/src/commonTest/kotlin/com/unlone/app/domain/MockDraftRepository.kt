package com.unlone.app.domain

import com.unlone.app.data.write.DraftRepository
import com.unlone.app.domain.entities.Draft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockDraftRepository : DraftRepository {
    override fun getAllDrafts(): Flow<List<Draft>> {
        return flowOf(listOf(Draft.mock))
    }

    override fun queryDraft(id: String): Flow<Draft> {
        return flowOf(Draft.mock)
    }

    override fun getLastOpenedDraft(): Flow<Draft?> {
        return flowOf(Draft.mock)
    }

    override suspend fun saveDraft(id: String?, title: String, body: String): Pair<String, String> {
        return Pair("1234", "5678")
    }

    override suspend fun updateLastOpenedTime(id: String) {

    }

    override suspend fun deleteDraft(id: String) {
    }

    override suspend fun updateDraftVersion(parentDraftId: String, title: String, body: String): Pair<String, String> {
        return Pair(parentDraftId, "newestVersionId")
    }

}