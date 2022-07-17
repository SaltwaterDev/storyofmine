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
            }).sheet(isPresented: $showLogin, content: {
                LoginEmailScreen(isPresented: $showLogin)
            })
        }}
    }
}

struct StoriesScreen_Previews: PreviewProvider {
    static var previews: some View {
        StoriesScreen()
    }
}
