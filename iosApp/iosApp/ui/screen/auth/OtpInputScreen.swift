//
//  OtpInputScreen.swift
//  iosApp
//
//  Created by Wah gor on 12/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct OtpInputScreen: View {
    @EnvironmentObject var signupViewModel: SignUpViewModel
    @Binding var otp: String
    let onOtpVerified: () -> ()
    @Binding var isVerified: Bool
    
    var body: some View {
        TextField(
            "Enter verify code",
            text: $otp
        )
        
        Button("Submit") {
//            signupViewModel.verifyOtp()
            onOtpVerified()
        }
        
        NavigationLink(
            destination: UsernameScreen(),
            isActive: $isVerified,
            label: {EmptyView()}
        )
    }
}

//struct OtpInputScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        OtpInputScreen(otp: .constant(""))
//    }
//}
