//
//  LoginScreen.swift
//  iosApp
//
//  Created by Daniel Sau on 5/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct LoginPwScreen: View {
    @Binding var password: String
    @Binding var errorMsg: String?
    @Binding var loading: Bool
    let onSignIn: () -> ()
    let dismissError: () -> ()
    
    var body: some View {
        VStack{
            SecureField("Password", text: $password)
                .padding()
                .autocapitalization(UITextAutocapitalizationType.none)
                .disableAutocorrection(true)
            
            Button("Sign In") {
                onSignIn()
            }.alert(item: $errorMsg) { Identifiable in
                Alert(
                    title: Text(errorMsg!),
                    dismissButton: .default(
                        Text("OK"),
                        action: dismissError
                    )
                )
            }
            
            if(loading){
                ProgressView()
            }
        }
    }
}

extension String: Identifiable {
    public typealias ID = Int
    public var id: Int {
        return hash
    }
}


//struct LoginScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        LoginScreen(signInViewModel: SignInViewModel())
//    }
//}
