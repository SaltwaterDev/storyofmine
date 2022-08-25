//
//  SwiftUIView.swift
//  iosApp
//
//  Created by Daniel Sau on 5/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ProfileScreen: View {
    @EnvironmentObject var authSetting: AuthViewModel
    let greet = Greeting().greeting()

    var body: some View {
        VStack{
//            let text = LocalizedStringKey(SharedRes.strings().my_string.desc().localized())
//            Text(text)
            
            List {
                    Text("A List Item")
                    Text("A Second List Item")
                    Text("A Third List Item")
                if (authSetting.isUserLoggedIn){
                    Button {
                        authSetting.signOut()
                    } label: {
                        Text(
                            LocalizedStringKey(SharedRes.strings().profile__sign_out.desc().localized())
                        )
                    }
                }
            }
        }.onAppear {
            print("Profile Screen: \(authSetting.isUserLoggedIn)")
        }
    }
}

struct ProfileScreen_Previews: PreviewProvider {
    static var previews: some View {
        ProfileScreen()
    }
}
