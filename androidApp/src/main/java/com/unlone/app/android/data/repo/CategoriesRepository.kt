package com.unlone.app.android.data.repo

import com.unlone.app.data.repo.UserPreferenceRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesRepository @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val authRepository: AuthRepository,
    externalScope: CoroutineScope,
) {

}




