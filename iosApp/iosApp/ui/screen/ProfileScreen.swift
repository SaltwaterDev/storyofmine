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
    @State var showSignoutAlert = false
    

    var body: some View {
        VStack{
            if (authSetting.isUserLoggedIn){
                usernameRow
            }
            
            List {
                Button{} label: {Text("My Stories")}
                Button{} label: {Text("Drafts")}
                Button{} label: {Text("Settings")}
                Button{} label: {Text("About Us")}

                if (authSetting.isUserLoggedIn){
                    Button {
                        showSignoutAlert = true
                    } label: {
                        Text(
                            LocalizedStringKey(SharedRes.strings().profile__sign_out.desc().localized())
                        )
                    }.confirmationDialog(
                        LocalizedStringKey(
                            SharedRes.strings().profile__sign_out_alert_title.desc().localized()
                        ),
                        isPresented: $showSignoutAlert,
                        titleVisibility: .visible
                    ) {
                        Button(
                            LocalizedStringKey(
                                SharedRes.strings().profile__sign_out.desc().localized()
                            ), role: .destructive
                        ) {
                            authSetting.signOut()
                        }
                        Button(
                            LocalizedStringKey(
                                SharedRes.strings().common__btn_cancel.desc().localized()
                            ),
                            role: .cancel
                        ) {
                            showSignoutAlert = false
                        }
                    }
                }
            }
        }.task {
            await authSetting.getUserName()
        }
    }
    
    var usernameRow: some View{
        HStack{
            Text(authSetting.username ?? "")
                .font(.largeTitle)
            Button{
                // todo
            } label: {
                Image(systemName: "pencil")
                    .font(.largeTitle)
            }
            Spacer()
        }.padding()
    }
}

struct ProfileScreen_Previews: PreviewProvider {
    static var previews: some View {
        ProfileScreen()
    }
}
