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
    
    var body: some View {
        
        let drag = DragGesture()
            .onEnded {
                if $0.translation.width < -100 {
                    withAnimation {
                        self.showMenu = false
                    }
                }
            }
        
        return NavigationView {
            GeometryReader { geometry in
                ZStack(alignment: .leading) {
                    VStack{
                        TextField("Untitled", text: $writingViewModel.title).textInputAutocapitalization(.never).disableAutocorrection(true).padding()
                        TextEditor(text: $writingViewModel.content).textInputAutocapitalization(.never).disableAutocorrection(true).padding()
                    }
                    if self.showMenu {
                        MenuView(showMenu: $showMenu, menuItems: writingViewModel.menuItemList, callback: writingViewModel.onMenuClicked)
                    }
                }.gesture(drag)
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
        }
    }
}

struct WritingScreen_Previews: PreviewProvider {
    static var previews: some View {
        WritingScreen()
    }
}
