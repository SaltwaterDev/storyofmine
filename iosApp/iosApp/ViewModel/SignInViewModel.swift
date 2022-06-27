//
//  SignInViewModel.swift
//  iosApp
//
//  Created by Daniel Sau on 12/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

class SignInViewModel: ObservableObject {

//    private var task: Task<Void, Never>?
    private let authRepo = AuthRepository() 
    var isEmailValid: Bool = false
    
    init(){
//        Task {await emailValidate()}
    }

    func emailValidate(email: String){
        print("Validate email")
        authRepo.signInEmail(email: email) {result,error  in
            switch (result){
                case is AuthResultAuthorized<KotlinUnit>:
                    self.isEmailValid = true
                    break
                case is AuthResultUnauthorized<KotlinUnit>:
                    self.isEmailValid = false
                    break
                case is AuthResultUnknownError<KotlinUnit>:
                    self.isEmailValid = false
                    break
                default:
                    self.isEmailValid = false
                    break
            }
        }
    }
    
    
    func signIn(email: String, password: String){
        authRepo.signIn(email: email, password: password, completionHandler: { (result, error) in

        })
    }
}
