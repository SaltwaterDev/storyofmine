//
//  SignUpScreen.swift
//  iosApp
//
//  Created by Daniel Sau on 10/7/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct SignUpScreen: View {
    @Binding var showSignup: Bool
    @ObservedObject var signupViewModel = SignUpViewModel()
    
    var body: some View {
        NavigationView{
            VStack{
            Text("Create account")
                .font(.largeTitle)
            
            TextField("Email", text: $signupViewModel.email, onEditingChanged: { editingChanged in
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
            
            if !signupViewModel.emailAvailable{
                Text("This email has been used")
            }
            SecureField("Password", text: $signupViewModel.password)
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .autocapitalization(UITextAutocapitalizationType.none)
                .disableAutocorrection(true)
            
            Text("Your password must contain at least one upper case letter, one lower case letter, and one number")
            
            SecureField("Confirm Password", text: $signupViewModel.confirmedPassword)
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .autocapitalization(UITextAutocapitalizationType.none)
                .disableAutocorrection(true)
            
            Button("Sign Up", action: {
                signupViewModel.signUp()}
            )
            .disabled(!signupViewModel.enabled)
            .alert(item: $signupViewModel.error) {Identifiable in
                    Alert(
                        title: Text(signupViewModel.error!),
                        dismissButton: .default(
                            Text("OK"),
                            action: {signupViewModel.dismissError()}
                        )
                    )
                }
            
            if signupViewModel.loading{
                ProgressView()
            }
            
            
            NavigationLink(
                destination: OtpEmailConfirmScreen(email: signupViewModel.email),
                isActive: $signupViewModel.accountCreated,
                label: {EmptyView()}
            )
        }
        }.onChange(of: signupViewModel.signUpSuccess) { signUpSuccess in
            if signUpSuccess{
                showSignup.toggle()
            }
        }.environmentObject(signupViewModel)
    }
}

//struct SignUpScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        SignUpScreen(signupViewModel: SignUpViewModel())
//    }
//}
