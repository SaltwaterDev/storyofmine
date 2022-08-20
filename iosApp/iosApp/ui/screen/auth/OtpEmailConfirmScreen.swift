//
//  OtpEmailConfirmScreen.swift
//  iosApp
//
//  Created by Wah gor on 12/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct OtpEmailConfirmScreen: View {
    // FIXME： to be removed
//    @EnvironmentObject var signupViewModel: SignUpViewModel
    
    let email: String
    @StateObject private var otpViewModel = OtpViewModel()
    @State var nextPage: Bool = false
        
    var body: some View {
        VStack{
            Text("Verify code will be sent to this email")
            Text(email)
            
            Button("Send") {
//                signupViewModel.generateOtp()
                otpViewModel.generateOtp(email: email)
                nextPage = true
            }
            
            NavigationLink(
                destination: OtpInputScreen(
                    otp: $otpViewModel.otp,
                    onOtpVerified: {otpViewModel.verifyOtp(email: email)},
                    isVerified: $otpViewModel.accountVerified
                ),
                isActive: $nextPage,
                label: {EmptyView()}
            )
        }
    }
}

//struct OtpEmailConfirmScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        OtpEmailConfirmScreen()
//    }
//}
