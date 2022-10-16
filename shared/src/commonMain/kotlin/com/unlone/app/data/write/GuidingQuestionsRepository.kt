package com.unlone.app.data.write

interface GuidingQuestionsRepository {
    suspend fun getGuidingQuestionList(): List<GuidingQuestion>
}


class GuidingQuestionsRepositoryImpl() : GuidingQuestionsRepository {


    override suspend fun getGuidingQuestionList(): List<GuidingQuestion> {
        return mockGuidingQsList
    }

    companion object{
        private val mockGuidingQsList = listOf(
            GuidingQuestion("Is there anything in the past that is related to the current issue?"),
            GuidingQuestion("Why do you think this is important to you?"),
            GuidingQuestion("How do you feel? Any bodily sensations? What do these feelings mean to you? "),
            GuidingQuestion("Does it give insight / implications on your personality?"),
            GuidingQuestion("What have you learnt from it? Can it be applied to other aspects in life?"),
            GuidingQuestion("What is your reaction to this? Do you usually react the same way in daily life?"),
            GuidingQuestion("What are your needs and wants reflected from this issue?"),
            GuidingQuestion("What can be done better?"),
            GuidingQuestion("What have you done to stop it from getting worse?"),
            GuidingQuestion("Do you have any alternatives for this?"),
        )
    }

}


// fixme: move to somewhere else
data class GuidingQuestion(
    val text: String
)