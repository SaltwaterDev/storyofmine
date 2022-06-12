package com.unlone.app.auth

sealed class AuthResult<T>(val data: T? = null, val errorMsg: String? = null) {
    class Authorized<T>(data: T? = null) : AuthResult<T>(data)
    class Unauthorized<T>(errorMsg: String?) : AuthResult<T>(errorMsg = errorMsg)
    class UnknownError<T> : AuthResult<T>()
}
