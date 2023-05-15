package com.example.app


import com.example.app.data.auth.AuthRepository
import com.example.app.di.appModule
import com.example.app.domain.useCases.write.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

// example
class GreetingHelper : KoinComponent {
    private val greeting: Greeting by inject()
    fun greet(): String = greeting.greeting()
}

class AuthRepositoryHelper : KoinComponent {
    private val authRepo: AuthRepository by inject()

    //    private val authRepo: AuthRepository by inject(named("iosAuthRepo")){ parametersOf(KMMContext)}
    fun authRepo(): AuthRepository = authRepo
    /*
    suspend fun signUpEmail(email: String) = authRepo.signUpEmail(email)
    suspend fun signUp(email: String, password: String) = authRepo.signUp(email, password)
    suspend fun signInEmail(email: String) = authRepo.signInEmail(email)
    suspend fun signIn(email: String, password: String) = authRepo.signIn(email, password)
    suspend fun authenticate() = authRepo.authenticate()
    fun signOut() = authRepo.signOut()*/
}

class TopicRepositoryHelper : KoinComponent {

}

class UseCasesHelper : KoinComponent {

    val getAllDraftsTitleUseCase: GetAllDraftsTitleUseCase by inject()
    val getLastOpenedUseCase: GetLastOpenedDraftUseCase by inject()
    val saveDraftUseCase: SaveDraftUseCase by inject()
    val createNewDraftUseCase: CreateNewDraftUseCase by inject()
    val queryDraftUseCase: QueryDraftUseCase by inject()
}


fun initKoin() = startKoin {
    modules(appModule())
}
