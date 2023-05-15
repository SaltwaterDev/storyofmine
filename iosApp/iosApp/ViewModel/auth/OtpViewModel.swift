//
//  OtpViewModel.swift
//  iosApp
//
//  Created by Wah gor on 20/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared


// TODO: replace it on otp screen
@MainActor
class OtpViewModel: ObservableObject{
//    let email: String
    private let authRepo = AuthRepositoryHelper().authRepo()
    
    @Published var error: String? = nil
    @Published var otp: String = ""
    @Published var accountVerified: Bool = false
    @Published var loading: Bool = false
    
//    init(email: String){
//        self.email = email
//    }
    
    func verifyOtp(email: String) {
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

    func generateOtp(email: String) {
        Task {
            try await authRepo.requestOtpEmail(email: email)
            print("otp sent")
        }
    }
}
