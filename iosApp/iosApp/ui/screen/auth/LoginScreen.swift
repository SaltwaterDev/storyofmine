//
//  LoginScreen.swift
//  iosApp
//
//  Created by Daniel Sau on 5/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct LoginScreen: View {
    @ObservedObject var signInViewModel: SignInViewModel
    @State private var password = ""
    
    var body: some View {
            VStack{
                TextField("Email: \(signInViewModel.email)", text: $signInViewModel.email).padding().disabled(true).autocapitalization(UITextAutocapitalizationType.none).disableAutocorrection(true)
                SecureField("Password", text: $password).padding().autocapitalization(UITextAutocapitalizationType.none).disableAutocorrection(true)
                Button("Sign In", action: {
                    signInViewModel.signIn(password: password)
                })
            }
    }
}

struct LoginScreen_Previews: PreviewProvider {
    static var previews: some View {
        LoginScreen(signInViewModel: SignInViewModel())
    }
}
