//
//  SignUpViewModel.swift
//  iosApp
//
//  Created by Daniel Sau on 13/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

class SignUpViewModel: ObservableObject {
    
    private let authRepo = AuthRepositoryHelper().authRepo()
    @Published var emailAvailable: Bool = false
    @Published var signUpSuccess: Bool = false
    
    init(){
    }
    
    func signUpEmailVerify(email: String){
        authRepo.signUpEmail(email: email, completionHandler: {result, error in
            print(result)
            switch (result){
                case is AuthResultAuthorized<KotlinUnit>:
                    print("Email available \(email)")
                    self.emailAvailable = true
                    break
                case is AuthResultUnauthorized<KotlinUnit>:
                print("Email not available")
                    self.emailAvailable = false
                    break
                case is AuthResultUnknownError<KotlinUnit>:
                print("Unknown error")
                    self.emailAvailable = false
                    break
                default:
                    self.emailAvailable = false
                    break
            }
        })
    }
    
    func signUp(email: String, password: String, completion: @escaping () -> (Void)){
        authRepo.signUp(email: email, password: password, completionHandler: {result, error in
            print(result)
            switch (result){
                case is AuthResultAuthorized<KotlinUnit>:
                    print("SignUp Success")
                    self.signUpSuccess = true
                    completion()
                    break
                case is AuthResultUnauthorized<KotlinUnit>:
                    print("Email not available")
                    self.signUpSuccess = false
                    break
                case is AuthResultUnknownError<KotlinUnit>:
                    print("Unknown error")
                    self.signUpSuccess = false
                    break
                default:
                    self.signUpSuccess = false
                    break
            }
        })
    }
}

