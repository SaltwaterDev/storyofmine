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
        NavigationView {
            VStack{
                TextField("Email", text: $email).padding().autocapitalization(UITextAutocapitalizationType.none).disableAutocorrection(true)
                NavigationLink(destination: SignUpScreen(), label: {
                    Text("Sign Up")
                })
                Button("Sign In", action: {
                    signInViewModel.emailValidate(email: email)
                })
                NavigationLink(destination: LoginScreen(signInViewModel: self.signInViewModel), isActive: $signInViewModel.userExists, label: {
                    EmptyView()
                })
            }
        }
    }
}

struct LoginEmailScreen_Previews: PreviewProvider {
    static var previews: some View {
        LoginEmailScreen()
    }
}
