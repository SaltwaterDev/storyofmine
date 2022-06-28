//
//  LoginScreen.swift
//  iosApp
//
//  Created by Daniel Sau on 5/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct LoginScreen: View {
    @State private var username = ""
    @State private var password = ""
    
    var body: some View {
        VStack{
            TextField("Username", text: $username).padding()
            TextField("Password", text: $password).padding()
            Button("Sign In", action: {
//                AuthRepositor
            })
        }
        
    }
}

struct LoginScreen_Previews: PreviewProvider {
    static var previews: some View {
        LoginScreen()
    }
}
