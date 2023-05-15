//
//  SetUsernameViewModel.swift
//  iosApp
//
//  Created by Wah gor on 20/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

@MainActor
class SetUsernameViewModel: ObservableObject{
    private let authRepo = AuthRepositoryHelper().authRepo()
    
    @Published var error: String? = nil
    @Published var username: String = ""
    @Published var succeed: Bool = false
    @Published var loading: Bool = false
        
    func setUsername(email: String) {
        loading = true
        Task{
            let result = try await authRepo.setUserName(email: email, username: username)
            switch (result) {
                case is AuthResultAuthorized<KotlinUnit>:
                    succeed = true
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
}
