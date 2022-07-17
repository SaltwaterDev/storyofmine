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
            Text(greet)
            if (authSetting.isUserLoggedIn){
                Button("Sign Out", action: {
                    authSetting.signOut()
                })
            }
        }
    }
}

struct ProfileScreen_Previews: PreviewProvider {
    static var previews: some View {
        ProfileScreen()
    }
}
