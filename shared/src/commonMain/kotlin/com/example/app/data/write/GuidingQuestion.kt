package com.example.app.data.write

import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data class GuidingQuestionListResponse(
    val data: List<GuidingQuestion>
)

@Serializable
data class GuidingQuestion(
    val text: String,
    val id: String = Random(0).toString(),
)




sealed class StaticResourceResult<T>(val data: T? = null, val errorMsg: String? = null) {
    class Success<T>(data: T? = null) : StaticResourceResult<T>(data = data)
    class Failed<T>(errorMsg: String?) : StaticResourceResult<T>(errorMsg = errorMsg)
    class UnknownError<T>(errorMsg: String?) : StaticResourceResult<T>(errorMsg = errorMsg)
}
