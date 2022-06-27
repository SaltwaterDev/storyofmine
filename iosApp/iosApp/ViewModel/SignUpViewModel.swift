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
    
    private let authRepo = AuthRepository()
    
    init(){
    }
    
    func signUpEmailVerify(email: String){
        authRepo.signUpEmail(email: email, completionHandler: {result, error in

        })
    }
    
    func signIn(email: String, password: String){
        authRepo.signUp(email: email, password: password, completionHandler: {result, error in
            
        })
    }
}

