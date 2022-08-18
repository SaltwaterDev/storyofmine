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
    private let validPasswordUseCase = ValidPasswordUseCase()
    
    @Published var email: String = ""
    @Published var password: String = ""
    @Published var confirmedPassword: String = ""
    @Published var emailAvailable: Bool = true
    @Published var signUpSuccess: Bool = false
    @Published var accountCreated: Bool = false
    @Published var loading: Bool = false
    @Published var pwError: Bool = false
    @Published var error: String? = nil
    @Published var otp: String = ""
    @Published var username: String = ""
    @Published var accountVerified: Bool = false
    
    func signUpEmailVerify(){
        Task{
            self.loading = true
            let result = try await authRepo.signUpEmail(email: self.email)
            print(result)
            switch (result){
            case is AuthResultAuthorized<KotlinUnit>:
                print("Email available \(self.email)")
                self.emailAvailable = true
                break
            case is AuthResultUnauthorized<KotlinUnit>:
                print("Email not available")
                self.error = result.errorMsg
                self.emailAvailable = false
                break
            case is AuthResultUnknownError<KotlinUnit>:
                print("Unknown error")
                self.error = result.errorMsg
                self.emailAvailable = false
                break
            default:
                self.emailAvailable = false
                break
            }
            self.loading = false
        }
    }
    
    func signUp(){
        guard validPasswordUseCase.invoke(password: password) else{
            pwError = true
            return
        }
        Task{
            self.loading = true
            let result = try await authRepo.signUp(email:email, password:password)
            print(result)
            switch (result){
            case is AuthResultAuthorized<KotlinUnit>:
                print("SignUp Success")
                self.accountCreated = true
                //                    completion()
                break
            case is AuthResultUnauthorized<KotlinUnit>:
                print("Email not available")
                self.accountCreated = false
                break
            case is AuthResultUnknownError<KotlinUnit>:
                print("Unknown error")
                self.accountCreated = false
                break
            default:
                self.accountCreated = false
                break
            }
            self.loading = false
        }
    }
    
    
    func dismissError(){
        self.error = nil
    }
    
    // MARK:- OTP
    func verifyOtp() {
        guard !otp.isEmpty else { return }
        loading = true
        Task {
            print(Int32(otp) ?? 0)
            let result = try await authRepo.verifyOtp(email:email, otp: Int32(otp) ?? 0)
            switch (result) {
            case is AuthResultAuthorized<KotlinUnit>:
                    accountVerified = true
                    print("email validate")
                break
            case is AuthResultUnauthorized<KotlinUnit>:
                    error = result.errorMsg
                break
            case is AuthResultUnknownError<KotlinUnit>:
                let rawMsg = result.errorMsg ?? ""
                error = "unknown error: " + rawMsg
                break
            default: break
            }
            loading = false
        }
    }

    func generateOtp() {
        Task {
            try await authRepo.requestOtpEmail(email:email)
            print("otp sent")
        }
    }
    
    // MARK: username
    func setUsername() {
        loading = true
        Task{
            let result = try await authRepo.setUserName(email: email, username: username)
            switch (result) {
                case is AuthResultAuthorized<KotlinUnit>:
                    signIn()
                    break
        
                case is AuthResultUnauthorized<KotlinUnit>:
                    error = result.errorMsg
                    break
                    
                case is AuthResultUnknownError<KotlinUnit>:
                    let rawMsg = result.errorMsg ?? ""
                    error = "unknown error: " + rawMsg
                    break
                default: break
            }
            loading = false
        }
    }

    private func signIn() {
        Task {
            let result = try await authRepo.signIn(email: email, password: password)
            switch(result){
            case is AuthResultAuthorized<KotlinUnit>:
                signUpSuccess  = true
                break
            case is AuthResultAuthorized<KotlinUnit>:
                error = result.errorMsg
                break
            case is AuthResultUnknownError<KotlinUnit>:
                let rawMsg = result.errorMsg ?? ""
                error = "unknown error: " + rawMsg
                break
            default:
                break
            }
        }
    }
    
    // MARK: reset sign up
    func removeSignUpRecord() {
        Task {
            let result = try await authRepo.removeUserRecordByEmail(email: email)
            switch(result){
            case is AuthResultAuthorized<KotlinUnit>:
                print("signup record removed")
                break
            case is AuthResultUnauthorized<KotlinUnit>:
                error = result.errorMsg
                break
            case is AuthResultUnknownError<KotlinUnit>:
                let rawMsg = result.errorMsg ?? ""
                error = "unknown error: " + rawMsg
                break
            default: break
            }
        }
    }
}


extension SignUpViewModel{
    var enabled: Bool {return !self.email.isEmpty &&
        !self.password.isEmpty &&
        self.emailAvailable &&
        (self.password == self.confirmedPassword) &&
        !self.loading
    }
}
