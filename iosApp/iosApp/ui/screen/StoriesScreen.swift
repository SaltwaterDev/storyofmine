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
    
    var body: some View {
        VStack{
            if (authSetting.isUserLoggedIn){
                Text(greet)
            }else{
                VStack(spacing: 20){
                    Text("Sign up to read other stories")
                        .font(.headline)
                    
                    Button("Sign Up", action: {
                        showSignup = true
                    }).sheet(isPresented: $showSignup, onDismiss: {
                        // authSetting.authenticate()
                    }, content: {
                        SignUpScreen(showSignup: $showSignup)
                    })
            
                    
                    Button("Login Instead", action: {
                        showLogin = true
                    }).sheet(isPresented: $showLogin, onDismiss: {
                         authSetting.authenticate()
                    }, content: {
                        LoginEmailScreen(showLogin: $showLogin){
                            NavigationLink(destination: SignUpScreen(showSignup: $showSignup)){
                                Text("Switch to Sign Up")
                            }
                        }
                    })
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
