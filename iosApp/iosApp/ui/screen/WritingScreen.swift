//
//  SwiftUIView.swift
//  iosApp
//
//  Created by Daniel Sau on 5/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct WritingScreen: View {
    @StateObject private var writingViewModel = WritingViewModel()
    @EnvironmentObject private var authSetting: AuthViewModel
    
    @State var showMenu = false
    @State var menuItemList: [MenuItemView] = []
    
    var body: some View {
        
        let MainView = VStack{
            TextField("Untitled", text: $writingViewModel.title).textInputAutocapitalization(.never).disableAutocorrection(true).padding()
            TextEditor(text: $writingViewModel.content).textInputAutocapitalization(.never).disableAutocorrection(true).padding()
        }
            .navigationBarTitle("Preview", displayMode: .inline)
            .navigationBarItems(leading: (
                Button(action: {
                    withAnimation {
                        self.showMenu.toggle()
                    }
                }) {
                    Image(systemName: "line.horizontal.3")
                        .imageScale(.large)
                }
            ), trailing: (Button(action: {
                withAnimation {
                    //
                }
            }) {
                Image(systemName: "paperplane")
                    .imageScale(.large)
            }))
        
        MenuView(showMenu: $showMenu, mainView: {
            MainView
        }, menuItems: menuItemList)
        .onAppear {
            writingViewModel.getAllDraftsTitle()
            menuItemList = writingViewModel.menuItemList.map({ title in
                MenuItemView(showMenu: $showMenu, title: title, callback:{ writingViewModel.onMenuClicked(indentifier: title) })
            }) + writingViewModel.draftList.map({ draft in
                MenuItemView(showMenu: $showMenu, title: draft.value, callback:{ writingViewModel.onMenuClicked(id: draft.key) })
            })
        }.onDisappear {
            writingViewModel.saveDraft()
        }.onChange(of: writingViewModel.draftList, perform: { value in
            menuItemList = writingViewModel.menuItemList.map({ title in
                MenuItemView(showMenu: $showMenu, title: title, callback:{ writingViewModel.onMenuClicked(indentifier: title) })
            }) + value.map({ draft in
                MenuItemView(showMenu: $showMenu, title: draft.value, callback:{ writingViewModel.onMenuClicked(id: draft.key) })
            })
        })
    }
}

struct WritingScreen_Previews: PreviewProvider {
    static var previews: some View {
        WritingScreen()
    }
}
