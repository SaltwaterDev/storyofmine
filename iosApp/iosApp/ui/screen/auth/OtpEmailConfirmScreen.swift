//
//  OtpEmailConfirmScreen.swift
//  iosApp
//
//  Created by Wah gor on 12/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct OtpEmailConfirmScreen: View {
    @EnvironmentObject var signupViewModel: SignUpViewModel
    @State var nextPage: Bool = false
    
    var body: some View {
        VStack{
            Text("Verify code will be sent to this email")
            Text(signupViewModel.email)
            
            Button("Send") {
                signupViewModel.generateOtp()
                nextPage = true
            }
            
            NavigationLink(
                destination: OtpInputScreen(),
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
