package com.unlone.app.data.write

sealed class StoryResult<T>(val data: T? = null, val errorMsg: String? = null) {
    class Success<T> : StoryResult<T>()
    class Failed<T>(errorMsg: String?) : StoryResult<T>(errorMsg = errorMsg)
}