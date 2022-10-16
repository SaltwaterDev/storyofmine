//
//  MenuView.swift
//  iosApp
//
//  Created by Daniel Sau on 4/9/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct MenuView<Content: View>: View {
    @Binding var showMenu: Bool
    let mainView: () -> Content
    
    var menuItems: [MenuItemView]
    var body: some View {
        
        let drag = DragGesture()
            .onEnded {
                if $0.translation.width < -100 {
                    withAnimation {
                        showMenu = false
                    }
                } else {
                    withAnimation {
                        showMenu = true
                    }
                }
            }
        
        let Menu = VStack(alignment: .leading) {
            ForEach(menuItems.indices) { index in
                if index == 0 {
                    menuItems[index].padding(.top, 100)
                } else {
                    menuItems[index]
                }
            }
            Spacer()
        }.padding()
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(red: 32/255, green: 32/255, blue: 32/255))
            .edgesIgnoringSafeArea(.all)
        
        return GeometryReader { geometry in
            ZStack(alignment: .leading) {
                NavigationView {
                    mainView()
                }
                .frame(width: geometry.size.width, height: geometry.size.height).disabled(showMenu ? true : false)
                    .offset(x: self.showMenu ? geometry.size.width/2 : 0)
                if self.showMenu {
                    Menu.frame(width: geometry.size.width/2).transition(.move(edge: .leading))
                }
            }.gesture(drag)
        }
    }
}

//struct MenuView_Previews: PreviewProvider {
//    static var previews: some View {
//        MenuView(menuItems: [], callback: {}())
//    }
//}
