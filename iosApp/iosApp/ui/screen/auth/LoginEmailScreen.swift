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
    @Binding var isPresented: Bool
    @ObservedObject var signInViewModel = SignInViewModel()
    @EnvironmentObject var authSetting: AuthViewModel
    
    @State private var showSignUp = false
    @State private var email = ""
    
    var body: some View {
            NavigationView{
            VStack{
                TextField("Email", text: $email).padding().autocapitalization(UITextAutocapitalizationType.none).disableAutocorrection(true)
                Button("Sign Up", action: {
                    showSignUp = true
                }).sheet(isPresented: $showSignUp, content: {
                    SignUpScreen(isPresented: $showSignUp)
                })
                Button("Sign In", action: {
                    signInViewModel.emailValidate(email: email)
                })
                NavigationLink(destination: LoginScreen(signInViewModel: self.signInViewModel), isActive: $signInViewModel.userExists, label: {
                    EmptyView()
                })
            }}.onChange(of: signInViewModel.signInSuccess){signInSuccess in
                if signInSuccess {
                    isPresented = false
                    authSetting.authenticate()
                }
            }
    }
}

struct LoginEmailScreen_Previews: PreviewProvider {
    static var previews: some View {
        LoginEmailScreen(isPresented: .constant(true))
    }
}
