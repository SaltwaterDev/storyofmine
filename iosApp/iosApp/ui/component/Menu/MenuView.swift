//
//  MenuView.swift
//  iosApp
//
//  Created by Daniel Sau on 4/9/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct MenuView: View {
    @Binding var showMenu: Bool
    var menuItems: [String]
    var callback: (_ title: String) async -> Void
    
//    init(menuItems: [String], callback: @escaping (_ title: String) async -> Void) {
//        self.menuItems = menuItems
//        self.callback = callback
//    }
    
    var body: some View {
        VStack(alignment: .leading) {
            ForEach(menuItems.indices) { index in
                if index == 0 {
                    MenuItemView(showMenu: $showMenu, title: menuItems[index], callback: callback).padding(.top, 100)
                } else {
                    MenuItemView(showMenu: $showMenu, title: menuItems[index], callback: callback)
                }
            }
            Spacer()
        }.padding()
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(red: 32/255, green: 32/255, blue: 32/255))
            .edgesIgnoringSafeArea(.all)
    }
}

//struct MenuView_Previews: PreviewProvider {
//    static var previews: some View {
//        MenuView(menuItems: [], callback: {}())
//    }
//}
