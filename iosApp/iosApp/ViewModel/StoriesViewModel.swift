//
//  StoriesViewModel.swift
//  iosApp
//
//  Created by Daniel Sau on 16/7/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

class StoriesViewModel: ObservableObject {
    private let authRepo = AuthRepositoryHelper().authRepo()
    @Published var isUserLoggedIn: Bool = false
    
        init(){

            authRepo.authenticate(completionHandler: {result, error in
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
                        break
                    default:
                        self.isUserLoggedIn = false
                        break
                }
            })
        }

}
