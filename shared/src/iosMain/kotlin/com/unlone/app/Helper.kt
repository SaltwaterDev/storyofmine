package com.unlone.app


import com.unlone.app.auth.AuthRepository
import com.unlone.app.auth.AuthResult
import com.unlone.app.di.appModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class GreetingHelper : KoinComponent {
    private val greeting : Greeting by inject()
    fun greet() : String = greeting.greeting()
}

class AuthRepository : KoinComponent {
    private val authRepo : AuthRepository by inject()
    suspend fun signUpEmail(email: String) = authRepo.signUpEmail(email)
    suspend fun signUp(email: String, password: String) = authRepo.signUp(email, password)
    suspend fun signInEmail(email: String) = authRepo.signInEmail(email)
    suspend fun signIn(email: String, password: String) = authRepo.signIn(email, password)
    suspend fun authenticate() = authRepo.authenticate()
    fun signOut() = authRepo.signOut()
}

fun initKoin(){
    startKoin {
        modules(appModule())
    }
}