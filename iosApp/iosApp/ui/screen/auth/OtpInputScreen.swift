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
    
    var body: some View {
        TextField(
            "Enter verofy code",
            text: $signupViewModel.otp
        )
        
        Button("Submit") {
            signupViewModel.verifyOtp()
        }
        
        NavigationLink(
            destination: UsernameScreen(),
            isActive: $signupViewModel.accountVerified,
            label: {EmptyView()}
        )
    }
}

//struct OtpInputScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        OtpInputScreen(otp: .constant(""))
//    }
//}
