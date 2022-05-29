package com.unlone.app.android.data.repo

import com.unlone.app.model.Draft
import javax.inject.Inject

class DraftRepository @Inject constructor() {
    fun getAllDraftTitles(): List<String> {
        return listOf(
            Draft.mock(),
            Draft.mock(),
            Draft.mock(),
        ).map { it.title }
    }
}