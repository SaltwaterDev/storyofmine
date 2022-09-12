//
//  MenuItem.swift
//  iosApp
//
//  Created by Daniel Sau on 4/9/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct MenuItemView: View {
    @Binding var showMenu: Bool
    var title = ""
    var callback: (_ title: String) async -> Void
    
    var body: some View {
        HStack {
            Image(systemName: "person")
                    .foregroundColor(.gray)
                    .imageScale(.large)
            Text(title)
                    .foregroundColor(.gray)
                    .font(.headline)
        }.onTapGesture {
            Task{
                await callback(title)
                showMenu = false
            }
        }
    }
}

//struct MenuItem_Previews: PreviewProvider {
//    static var previews: some View {
//        MenuItemView()
//    }
//}
