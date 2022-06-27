//
//  LoginScreen.swift
//  iosApp
//
//  Created by Daniel Sau on 5/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct LoginEmailScreen: View {
    @ObservedObject var signInViewModel = SignInViewModel()

    @State private var email = ""
    
    var body: some View {
        VStack{
            TextField("Email", text: $email).padding().autocapitalization(UITextAutocapitalizationType.none).disableAutocorrection(true)
            Button("Sign In", action: {
                signInViewModel.emailValidate(email: email)
            })
        }
        
    }
}

struct LoginEmailScreen_Previews: PreviewProvider {
    static var previews: some View {
        LoginEmailScreen()
    }
}
