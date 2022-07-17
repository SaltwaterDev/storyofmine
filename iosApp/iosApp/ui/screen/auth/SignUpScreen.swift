//
//  SignUpScreen.swift
//  iosApp
//
//  Created by Daniel Sau on 10/7/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct SignUpScreen: View {
    @Binding var isPresented: Bool
    @ObservedObject var signupViewModel = SignUpViewModel()
    @State private var email = ""
    @State private var password = ""
    
    var body: some View {
        VStack{
            TextField("Email", text: $email, onCommit: {
                signupViewModel.signUpEmailVerify(email: email)
            }).padding().autocapitalization(UITextAutocapitalizationType.none).disableAutocorrection(true)
            if signupViewModel.emailAvailable{
                SecureField("Password", text: $password).padding().autocapitalization(UITextAutocapitalizationType.none).disableAutocorrection(true)
                Button("Sign Up", action: {
                    signupViewModel.signUp(email: email, password: password, completion: {
                        isPresented = false
                    })})
            }
        }
    }
}

struct SignUpScreen_Previews: PreviewProvider {
    static var previews: some View {
        SignUpScreen(isPresented: .constant(true))
    }
}
