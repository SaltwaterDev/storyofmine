package com.unlone.app.android.data.repo

import com.unlone.app.model.Draft

class DraftRepository {
    fun getAllDraftTitles(): List<String> {
        return listOf(
            Draft.mock(),
            Draft.mock(),
            Draft.mock(),
        ).map { it.title }
    }
}