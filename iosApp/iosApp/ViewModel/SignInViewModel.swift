//
//  SignInViewModel.swift
//  iosApp
//
//  Created by Daniel Sau on 12/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

public class SignInViewModel: ObservableObject {

    private let authRepo: AuthRepository = AuthRepositoryHelper().authRepo()
    @Published var email: String = ""
    @Published var userExists: Bool = false
    @Published var signInSuccess: Bool = false
    @Published var loading: Bool = false
    
    init(){
//        Task {await emailValidate()}
    }

    func emailValidate(email: String){
        print("Validate email")
        self.loading = true
        authRepo.signInEmail(email: email) {result,error  in
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
    
    
    func signIn(password: String){
        self.loading = true
        authRepo.signIn(email: self.email, password: password, completionHandler: {result, error in
            print(result)
            switch (result){
                case is AuthResultAuthorized<KotlinUnit>:
                    print("Login Success")
                    self.signInSuccess = true
                case is AuthResultUnauthorized<KotlinUnit>:
                    print("Incorrect Password")
                    self.signInSuccess = false
                case is AuthResultUnknownError<KotlinUnit>:
                    print("Unknown error")
                    self.signInSuccess = false
                    break
                default:
                    self.signInSuccess = false
                    break
            }
            self.loading = false
        })
    }
}
