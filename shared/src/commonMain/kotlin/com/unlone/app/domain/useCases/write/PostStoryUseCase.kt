package com.unlone.app.domain.useCases.write

import com.unlone.app.data.auth.AuthRepository
import com.unlone.app.data.auth.AuthResult
import com.unlone.app.data.write.StoryRepository
import com.unlone.app.data.write.StoryResult

class PostStoryUseCase(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        title: String,
        content: String,
        topic: String,
        isPublished: Boolean,
        commentAllowed: Boolean,
        saveAllowed: Boolean,
    ): StoryResult<Unit> {
        return when (authRepository.authenticate()) {
            is AuthResult.Authorized -> storyRepository.postStory(
                authRepository.getJwt()!!,
                title,
                content,
                topic,
                isPublished,
                commentAllowed,
                saveAllowed
            )
            is AuthResult.Unauthorized -> StoryResult.Failed("user not logged in")
            is AuthResult.UnknownError -> StoryResult.Failed("unknown error")
        }
    }
}