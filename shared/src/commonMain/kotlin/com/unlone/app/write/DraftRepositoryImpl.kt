package com.unlone.app.write

class DraftRepositoryImpl: DraftRepository {
    override fun getAllDraftTitles(): List<String> {
        return listOf(
            Draft.mock(),
            Draft.mock(),
            Draft.mock(),
        ).map { it.title }
    }

    override suspend fun saveDraft(title: String, content: String) {
        /*TODO("Not yet implemented")*/
    }
}