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
    @Binding var showLogin: Bool
    @ObservedObject var signInViewModel = SignInViewModel()
    @EnvironmentObject var authSetting: AuthViewModel
    let onSignUp: () -> ()
    
    var body: some View {
        NavigationView{
            VStack{
                Text("Login").font(.largeTitle)
            
                TextField("Email", text: $signInViewModel.email)
                    .padding()
                    .autocapitalization(UITextAutocapitalizationType.none)
                    .disableAutocorrection(true)
                    .textContentType(.emailAddress)
                    .keyboardType(.emailAddress)
                
                Button(
                    "Sign In",
                    action: {signInViewModel.emailValidate()}
                ).disabled(signInViewModel.email.isEmpty)
                

                
                NavigationLink(
                    destination: LoginScreen(password: $signInViewModel.password, onSignIn: {signInViewModel.signIn()}),
                    isActive: $signInViewModel.userExists,
                    label: {EmptyView()}
                )
                
                if(signInViewModel.loading){
                    ProgressView()
                }
            }
        }.onChange(of: signInViewModel.signInSuccess){signInSuccess in
            if signInSuccess {
                showLogin.toggle()
            }
        }
    }
}


//
//struct LoginEmailScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        LoginEmailScreen(isPresented: .constant(true))
//    }
//}
