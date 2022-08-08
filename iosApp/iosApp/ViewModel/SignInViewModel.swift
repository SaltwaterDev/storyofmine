//
//  SignInViewModel.swift
//  iosApp
//
//  Created by Daniel Sau on 12/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared


@MainActor
public class SignInViewModel: ObservableObject {

    private let authRepo: AuthRepository = AuthRepositoryHelper().authRepo()
    @Published var email: String = ""
    @Published var password: String = ""
    @Published var userExists: Bool = false
    @Published var signInSuccess: Bool = false
    @Published var loading: Bool = false
    
    func emailValidate(){
        print("Validate email")
        self.loading = true
        Task{
            let result = try await authRepo.signInEmail(email: email)
            print(result)
            switch (result){
                case is AuthResultAuthorized<KotlinUnit>:
                    print("Validated email \(email)")
                    self.email = email
                    self.userExists = true
                    print("Validated email: \(self.userExists)")
                    break
                case is AuthResultUnauthorized<KotlinUnit>:
                    print("Invalid email")
                    self.userExists = false
                    break
                case is AuthResultUnknownError<KotlinUnit>:
                    print("Unknown error")
                    self.userExists = false
                    break
                default:
                    self.userExists = false
                    break
            }
            self.loading = false
        }
    }
    
    
    func signIn(){
        self.loading = true
        Task{
            let result = try await authRepo.signIn(email: email, password: self.password)
            print(result)
            switch (result){
                case is AuthResultAuthorized<KotlinUnit>:
                    print("Login Success")
                    self.signInSuccess = true
                    break
                case is AuthResultUnauthorized<KotlinUnit>:
                    print("Incorrect Password")
                    self.signInSuccess = false
                    break
                case is AuthResultUnknownError<KotlinUnit>:
                    print("Unknown error")
                    self.signInSuccess = false
                    break
                default:
                    self.signInSuccess = false
                    break
            }
            self.loading = false
        }
    }
}
