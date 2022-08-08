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
    @Binding var password: String
    let onSignIn: () -> ()
    
    var body: some View {
        VStack{
            SecureField("Password", text: $password)
                .padding()
                .autocapitalization(UITextAutocapitalizationType.none)
                .disableAutocorrection(true)
            
            Button("Sign In") {
                onSignIn()
            }
        }
    }
}

//struct LoginScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        LoginScreen(signInViewModel: SignInViewModel())
//    }
//}
