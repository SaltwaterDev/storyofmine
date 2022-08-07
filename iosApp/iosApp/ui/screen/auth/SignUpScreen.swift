//
//  SignUpScreen.swift
//  iosApp
//
//  Created by Daniel Sau on 10/7/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct SignUpScreen: View {
//    @Binding var isPresented: Bool
    @ObservedObject var signupViewModel: SignUpViewModel
    
    var body: some View {
        VStack{
            Text("Create account")
                .font(.title)
            
            TextField("Email", text: $signupViewModel.uiState.email, onEditingChanged: { editingChanged in
                if !editingChanged{     // focus removed
                    signupViewModel.signUpEmailVerify()
                }
            })
            .textContentType(.emailAddress)
            .keyboardType(.emailAddress)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding()
            .autocapitalization(UITextAutocapitalizationType.none)
            .disableAutocorrection(true)
            
            if !signupViewModel.uiState.emailAvailable{
                Text("This email has been used")
            }
            SecureField("Password", text: $signupViewModel.uiState.password)
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .autocapitalization(UITextAutocapitalizationType.none)
                .disableAutocorrection(true)
            
            SecureField("Confirm Password", text: $signupViewModel.uiState.confirmedPassword)
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .autocapitalization(UITextAutocapitalizationType.none)
                .disableAutocorrection(true)
            
            Button("Sign Up", action: {
                signupViewModel.signUp()}
            ).disabled(!signupViewModel.uiState.enabled)
            
            if signupViewModel.uiState.loading{
                ProgressView()
            }
        }
         
    }
}

struct SignUpScreen_Previews: PreviewProvider {
    static var previews: some View {
        SignUpScreen(signupViewModel: SignUpViewModel())
    }
}
