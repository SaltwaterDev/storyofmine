//
//  SwiftUIView.swift
//  iosApp
//
//  Created by Daniel Sau on 5/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct StoriesScreen: View {
    @EnvironmentObject var authSetting: AuthViewModel
    let greet = Greeting().greeting()
    @State private var showLogin = false
    @State private var showSignup = false
    @ObservedObject var signupViewModel = SignUpViewModel()
    
    var body: some View {
        VStack{
            if (authSetting.isUserLoggedIn){
                Text(greet)
            }else{
                HStack{
                    Button("Sign In", action: {
                        showLogin = true
                    }).sheet(isPresented: $showLogin, onDismiss: {
                        authSetting.authenticate()
                    }, content: {
                        LoginEmailScreen(isPresented: $showLogin)
                    })
                    
                    Button("Sign Up", action: {
                        showSignup = true
                    }).sheet(isPresented: $showSignup, onDismiss: {
    //                    authSetting.authenticate()
                    }, content: {
                        SignUpScreen(signupViewModel: signupViewModel)
                    }).onChange(of: signupViewModel.uiState.signUpSuccess) { signUpSuccess in
                        if signUpSuccess{
                            showSignup.toggle()
                        }
                    }
                }
            }
        }.onAppear {
            print("Stories Screen: \(authSetting.isUserLoggedIn)")
        }
    }
}

struct StoriesScreen_Previews: PreviewProvider {
    static var previews: some View {
        StoriesScreen()
    }
}
