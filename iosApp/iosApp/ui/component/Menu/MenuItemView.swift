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
    let callback: () -> ()
    
    var body: some View {
        HStack {
            Image(systemName: "person")
                .foregroundColor(.gray)
                .imageScale(.large)
            Text(title)
                .foregroundColor(.gray)
                .font(.headline)
        }.onTapGesture {
            Task {
                callback()
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
