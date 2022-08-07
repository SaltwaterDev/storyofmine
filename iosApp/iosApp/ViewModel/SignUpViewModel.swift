//
//  SignUpViewModel.swift
//  iosApp
//
//  Created by Daniel Sau on 13/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

@MainActor
class SignUpViewModel: ObservableObject {
    
    private let authRepo = AuthRepositoryHelper().authRepo()
    @Published var uiState: SignUpUiState = SignUpUiState()
    
    
    func signUpEmailVerify(){
        Task{
            self.uiState.loading = true
            let result = try await authRepo.signUpEmail(email: uiState.email)
            print(result)
            switch (result){
            case is AuthResultAuthorized<KotlinUnit>:
                print("Email available \(self.uiState.email)")
                self.uiState.emailAvailable = true
                break
            case is AuthResultUnauthorized<KotlinUnit>:
                print("Email not available")
                self.uiState.emailAvailable = false
                break
            case is AuthResultUnknownError<KotlinUnit>:
                print("Unknown error")
                self.uiState.emailAvailable = false
                break
            default:
                self.uiState.emailAvailable = false
                break
            }
            self.uiState.loading = false
        }
    }
    
    func signUp(){
        Task{
            self.uiState.loading = true
            let result = try await authRepo.signUp(email:uiState.email, password:uiState.password)
            print(result)
            switch (result){
            case is AuthResultAuthorized<KotlinUnit>:
                print("SignUp Success")
                self.uiState.signUpSuccess = true
                //                    completion()
                break
            case is AuthResultUnauthorized<KotlinUnit>:
                print("Email not available")
                self.uiState.signUpSuccess = false
                break
            case is AuthResultUnknownError<KotlinUnit>:
                print("Unknown error")
                self.uiState.signUpSuccess = false
                break
            default:
                self.uiState.signUpSuccess = false
                break
            }
            self.uiState.loading = false
        }
    }
    
    func validatePw(){
        // todo
    }
}

struct SignUpUiState {
    var email: String = ""
    var password: String = ""
    var confirmedPassword: String = ""
    var emailAvailable: Bool = true
    var signUpSuccess: Bool = false
    var loading: Bool = false
    
}

extension SignUpUiState{
    var enabled: Bool {return !self.email.isEmpty &&
        !self.password.isEmpty &&
        self.emailAvailable &&
        (self.password == self.confirmedPassword) &&
        !self.loading
    }
}
