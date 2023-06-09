package com.example.app.domain

import com.example.app.data.write.DraftRepository
import com.example.app.domain.entities.Draft
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

    override suspend fun createNewDraft(title: String, body: String): Pair<String, String> {
        return Pair("1234", "5678")
    }


    override suspend fun updateLastOpenedTime(id: String) {

    }

    override suspend fun deleteDraft(id: String) {
    }

    override suspend fun updateDraftVersion(draftId: String, title: String, body: String): Pair<String, String> {
        return Pair(draftId, "newestVersionId")
    }

    override suspend fun addNewVersionToDraft(
        id: String,
        title: String,
        body: String
    ): Pair<String?, String?> {
        return Pair(id, "newestVersionId")
    }

}