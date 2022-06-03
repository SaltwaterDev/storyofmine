package com.unlone.app.android.data.repo

import com.unlone.app.auth.AuthRepository
import kotlinx.coroutines.CoroutineScope

class CategoriesRepository(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val authRepository: AuthRepository,
    externalScope: CoroutineScope,
) {

}




