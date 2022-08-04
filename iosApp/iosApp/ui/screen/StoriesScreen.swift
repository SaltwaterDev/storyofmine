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
    
    var body: some View {
        VStack{
            if (authSetting.isUserLoggedIn){
                Text(greet)
            }else{
                Button("Sign In", action: {
                    showLogin = true
                }).sheet(isPresented: $showLogin, onDismiss: {
                    authSetting.authenticate()
                }, content: {
                    LoginEmailScreen(isPresented: $showLogin)
                })
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
