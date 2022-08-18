//
//  UsernameScreen.swift
//  iosApp
//
//  Created by Wah gor on 14/8/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct UsernameScreen: View {
    @EnvironmentObject var signupViewModel: SignUpViewModel
    
    var body: some View {
        Text("You are verified! Please eneter your username")
        TextField("Username", text: $signupViewModel.username)
        
        Button("Finish") {
            signupViewModel.setUsername()
        }
    }
}

//struct UsernameScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        UsernameScreen(username: .constant(""))
//    }
//}
