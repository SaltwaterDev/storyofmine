//
//  AuthViewModel.swift
//  iosApp
//
//  Created by Daniel Sau on 17/7/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

@MainActor
class AuthViewModel: ObservableObject {
    private let authRepo = AuthRepositoryHelper().authRepo()
    @Published private(set) var isUserLoggedIn: Bool = false
    @Published private(set) var username: String? = nil
    
    init(){
        self.authenticate()
    }
    
    func authenticate() {
        Task{
            let result = try await authRepo.authenticate ()
            print(result)
            switch (result){
                case is AuthResultAuthorized<KotlinUnit>:
                    print("Authorized")
                    self.isUserLoggedIn = true
                case is AuthResultUnauthorized<KotlinUnit>:
                    print("Unauthorized")
                    self.isUserLoggedIn = false
                case is AuthResultUnknownError<KotlinUnit>:
                    print("Unknown error")
                    self.isUserLoggedIn = false
                default:
                    self.isUserLoggedIn = false
            }
        }
    }

    func signOut() {
        authRepo.signOut()
        self.authenticate()
    }
    
    func getUserName() async {
        do{
            let getUsernameResponse = try await authRepo.getUsername()
            switch (getUsernameResponse){
                case is AuthResultAuthorized<NSString>:
                    if( getUsernameResponse.data != nil){
                        username = getUsernameResponse.data as String?
                    }
                default: return // todo
            }
        }catch{
            print(error)
        }
    }
}
