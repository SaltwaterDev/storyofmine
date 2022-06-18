package com.unlone.app.write


interface DraftRepository {
    fun getAllDraftTitles(): List<String>
    suspend fun saveDraft(title: String, content: String)
}