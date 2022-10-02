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
                } else {
                    withAnimation {
                        self.showMenu = true
                    }
                }
            }
        
        return GeometryReader { geometry in
            ZStack(alignment: .leading) {
                NavigationView {
                    VStack{
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
                }
                .frame(width: geometry.size.width, height: geometry.size.height).disabled(self.showMenu ? true : false)
                    .offset(x: self.showMenu ? geometry.size.width/2 : 0)
                if self.showMenu {
                    MenuView(showMenu: $showMenu, menuItems: writingViewModel.menuItemList, callback: writingViewModel.onMenuClicked).frame(width: geometry.size.width/2).transition(.move(edge: .leading))
                }
            }.gesture(drag)
        }
    }
}

struct WritingScreen_Previews: PreviewProvider {
    static var previews: some View {
        WritingScreen()
    }
}
